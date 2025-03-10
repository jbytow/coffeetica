import { useContext, useEffect, useState } from "react";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { ReviewBox } from "./components/ReviewBox/ReviewBox";
import { LatestReviews } from "./components/LatestReviews";
import { ReviewRequestDTO } from "../../models/ReviewRequestDTO";
import { ReviewDTO } from "../../models/ReviewDTO";
import { AuthContext } from "../../auth/AuthContext";
import { Link, useParams } from "react-router-dom";
import { StarsDisplay } from "../Utils/StarsDisplay";
import { CoffeeDetailsDTO } from "../../models/CoffeeDetailsDTO";

/**
 * Displays detailed information about a single coffee, along with its reviews.
 */
export const CoffeePage = () => {
  const [coffee, setCoffee] = useState<CoffeeDetailsDTO | undefined>();
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);
  const [userReview, setUserReview] = useState<ReviewDTO | null>(null);

  const { isAuthenticated } = useContext(AuthContext);
  const token = localStorage.getItem("token");

  // Extract the coffeeId from the URL parameters
  const { id } = useParams<{ id: string }>();
  const coffeeId = id ? Number(id) : null;

  // Fetch aggregated coffee details from the backend
  useEffect(() => {
    const fetchCoffeeDetails = async () => {
      if (!coffeeId) return;
      try {
        const response = await apiClient.get<CoffeeDetailsDTO>(`/coffees/${coffeeId}`);
        setCoffee(response.data);
      } catch (error: any) {
        setHttpError(error.message);
      } finally {
        setIsLoading(false);
      }
    };
    fetchCoffeeDetails();
  }, [coffeeId]);

  // Fetch the current user's review for the coffee (if authenticated)
  useEffect(() => {
    const fetchUserReview = async () => {
      if (!isAuthenticated || !token || !coffeeId || isNaN(coffeeId)) {
        return;
      }
      try {
        const response = await apiClient.get(`/reviews/user?coffeeId=${coffeeId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.status === 200) {
          setUserReview(response.data);
        }
      } catch (error) {
        console.error("Error fetching user review:", error);
      }
    };
    fetchUserReview();
  }, [isAuthenticated, coffeeId, token]);

  // Functions to create, update, and delete reviews
  const createReview = async (reviewData: ReviewRequestDTO) => {
    if (!token) {
      alert("You must be logged in to create a review.");
      return;
    }
    try {
      const response = await apiClient.post<ReviewDTO>("/reviews", reviewData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUserReview(response.data);
    } catch (error: any) {
      setHttpError(error.message);
    }
  };

  const updateReview = async (reviewId: number, updatedReview: ReviewRequestDTO) => {
    if (!token) {
      alert("You must be logged in to update a review.");
      return;
    }
    try {
      const response = await apiClient.put<ReviewDTO>(
        `/reviews/${reviewId}`,
        updatedReview,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setUserReview(response.data);
    } catch (error: any) {
      setHttpError(error.message);
    }
  };

  const deleteReview = async (reviewId: number) => {
    if (!token) {
      alert("You must be logged in to delete a review.");
      return;
    }
    try {
      await apiClient.delete(`/reviews/${reviewId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUserReview(null);
    } catch (error: any) {
      setHttpError(error.message);
    }
  };

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p className="alert alert-danger">{httpError}</p>
      </div>
    );
  }

  // Extract aggregated fields from CoffeeDetailsDTO for easier use in the UI
  const averageRating = coffee?.averageRating ?? 0;
  const totalReviewsCount = coffee?.totalReviewsCount ?? 0;
  const latestReviews = coffee?.latestReviews ?? [];

  return (
    <div className="container mt-5">
      <div className="row">
        {/* Column for coffee image */}
        <div className="col-12 col-md-4 col-lg-3 mb-3" style={{ maxWidth: "400px" }}>
          {coffee?.imageUrl ? (
            <div className="ratio" style={{ aspectRatio: "300 / 400" }}>
              <img
                src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
                alt="Coffee"
                style={{ width: "100%", height: "100%" }}
              />
            </div>
          ) : (
            <div>No Image Available</div>
          )}
        </div>

        {/* Column for coffee details */}
        <div className="col-12 col-md-8 col-lg-5 mb-3">
          <h2>{coffee?.name}</h2>
          {totalReviewsCount > 0 ? (
            <div className="d-flex align-items-center mb-3">
              <StarsDisplay rating={averageRating} />
              <span className="ms-2 text-muted">
                ({totalReviewsCount} reviews)
              </span>
            </div>
          ) : (
            <p className="text-muted">No reviews yet</p>
          )}
          {coffee?.roastery && (
            <p>
              <strong>Roastery: </strong>
              <Link to={`/roasteries/${coffee.roastery.id}`}>
                {coffee.roastery.name}
              </Link>
            </p>
          )}
          <p>
            <strong>Region:</strong> {coffee?.region}
          </p>
          <p>
            <strong>Country of Origin:</strong> {coffee?.countryOfOrigin}
          </p>
          <p>
            <strong>Roast Level:</strong> {coffee?.roastLevel}
          </p>
          <p>
            <strong>Flavor Profile:</strong> {coffee?.flavorProfile}
          </p>
          <p>
            <strong>Notes:</strong> {coffee?.flavorNotes?.join(", ")}
          </p>
          <p>
            <strong>Processing Method:</strong> {coffee?.processingMethod}
          </p>
          <p>
            <strong>Production Year:</strong> {coffee?.productionYear}
          </p>
        </div>

        {/* Column for the review box component */}
        <div className="col-12 col-lg-4">
          <ReviewBox
            coffee={coffee}
            userReview={userReview}
            createReview={createReview}
            updateReview={updateReview}
            deleteReview={deleteReview}
          />
        </div>
      </div>

      <hr />

      {/* LatestReviews for coffee => show userName, i.e. showCoffeeInsteadOfUser={false} */}
      <LatestReviews
        reviews={latestReviews}
        coffeeId={coffee?.id}
        mobile={false}
        showCoffeeInsteadOfUser={false}
      />
    </div>
  );
};