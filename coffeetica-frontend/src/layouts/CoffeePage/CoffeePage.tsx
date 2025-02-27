import { useContext, useEffect, useState } from "react";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { ReviewBox } from "./components/ReviewBox/ReviewBox";
import { LatestReviews } from "./components/LatestReviews";
import { ReviewRequestDTO } from "../../models/ReviewRequestDTO";
import { ReviewDTO } from "../../models/ReviewDTO";
import { AuthContext } from "../../auth/AuthContext";
import { useParams } from "react-router-dom";
import { StarsDisplay } from "../Utils/StarsDisplay";
import { CoffeeDetailsDTO } from "../../models/CoffeeDetailsDTO";

export const CoffeePage = () => {
  const [coffee, setCoffee] = useState<CoffeeDetailsDTO | undefined>();
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);
  const [userReview, setUserReview] = useState<ReviewDTO | null>(null);

  const { isAuthenticated } = useContext(AuthContext);
  const token = localStorage.getItem("token");

  // Extracting coffeeId from the URL
  const { id } = useParams<{ id: string }>();
  const coffeeId = id ? Number(id) : null;

  // Fetch aggregated coffee details from the endpoint returning CoffeeDetailsDTO
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

  // Fetch user review (remains unchanged)
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

  // ---- 3 FUNCTIONS FOR HANDLING REVIEWS (create, update, delete) ----
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
  // ---- END OF FUNCTIONS ----

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  // use aggregated fields from CoffeeDetailsDTO
  const averageRating = coffee?.averageRating ?? 0;
  const totalReviewsCount = coffee?.totalReviewsCount ?? 0;
  const latestReviews = coffee?.latestReviews ?? [];

  return (
    <div className="container mt-5">
      {/* Rząd z obrazkiem, opisem i boxem recenzji - widoczne w każdej szerokości, a Bootstrap sam dostosuje kolumny */}
      <div className="row">
        {/* Kolumna z obrazkiem kawy */}
        <div className="col-12 col-md-4 col-lg-3 mb-3" style={{ maxWidth: "400px" }}>
  {coffee?.imageUrl ? (
    <div
      className="ratio"
      style={{
        aspectRatio: "300 / 400",
      }}
    >
      <img
        src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
        alt="Coffee"
        style={{
          width: "100%",
          height: "100%",
        }}
      />
    </div>
  ) : (
    <div>No Image Available</div>
  )}
</div>

        {/* Kolumna z informacjami o kawie */}
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
          <p>
            <strong>Country of Origin:</strong> {coffee?.countryOfOrigin}
          </p>
          <p>
            <strong>Region:</strong> {coffee?.region}
          </p>
          <p>
            <strong>Roast Level:</strong> {coffee?.roastLevel}
          </p>
          <p>
            <strong>Flavor Profile:</strong> {coffee?.flavorProfile}
          </p>
          <p>
            <strong>Notes:</strong>{" "}
            {coffee?.flavorNotes?.join(", ")}
          </p>
          <p>
            <strong>Processing Method:</strong> {coffee?.processingMethod}
          </p>
          <p>
            <strong>Production Year:</strong> {coffee?.productionYear}
          </p>
        </div>

        {/* Kolumna z ReviewBox */}
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

      {/* Komponent z najnowszymi recenzjami */}
      <LatestReviews
        reviews={latestReviews}
        coffeeId={coffee?.id}
        mobile={false} 
      />
    </div>
  );
};