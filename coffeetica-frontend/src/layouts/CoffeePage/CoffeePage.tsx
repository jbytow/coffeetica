import { useContext, useEffect, useState } from "react";
import { CoffeeDTO } from "../../models/CoffeeDTO";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { ReviewBox } from "./components/ReviewBox/ReviewBox";
import { LatestReviews } from "./components/LatestReviews";
import { ReviewRequestDTO } from "../../models/ReviewRequestDTO";
import { ReviewDTO } from "../../models/ReviewDTO";
import { AuthContext } from "../../auth/AuthContext";
import { useParams } from "react-router-dom";
import { StarsDisplay } from "../Utils/StarsDisplay";


export const CoffeePage = () => {
  const [coffee, setCoffee] = useState<CoffeeDTO | undefined>();
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);

  const [userReview, setUserReview] = useState<ReviewDTO | null>(null);

  const { isAuthenticated } = useContext(AuthContext);
  const token = localStorage.getItem("token");

  // Extracting coffeeId from the current URL
  const { id } = useParams<{ id: string }>();
  const coffeeId = id ? Number(id) : null;

  // average rating calculation
  const calculateAverageRating = (reviews: ReviewDTO[]): number => {
    if (!reviews.length) return 0;
    const total = reviews.reduce((sum, review) => sum + review.rating, 0);
    return Math.round((total / reviews.length) * 2) / 2;
  };

  const averageRating = coffee?.reviews ? calculateAverageRating(coffee.reviews) : 0;

  // Fetch coffee details
  useEffect(() => {
    const fetchCoffee = async () => {
      try {
        const response = await apiClient.get<CoffeeDTO>(`/coffees/${coffeeId}`);
        setCoffee(response.data);
      } catch (error: any) {
        setHttpError(error.message);
      } finally {
        setIsLoading(false);
      }
    };
    fetchCoffee();
  }, [coffeeId]);

  // review download (if the user is logged in)
  useEffect(() => {
    const fetchUserReview = async () => {
      if (!isAuthenticated || !token || !coffeeId || isNaN(coffeeId)) {
        return;
      }
  
      console.log(`Fetching review for coffeeId: ${coffeeId}`);
  
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

// ---- 3 FUNCTIONS FOR HANDLING REVIEWS ----
const createReview = async (reviewData: ReviewRequestDTO) => {
  // Creates a NEW review
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
  // UPDATES an EXISTING review
  if (!token) {
    alert("You must be logged in to update a review.");
    return;
  }
  try {
    const response = await apiClient.put<ReviewDTO>(
      `/reviews/${reviewId}`, // PUT /api/reviews/{id}
      updatedReview,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );
    setUserReview(response.data);
  } catch (error: any) {
    setHttpError(error.message);
  }
};

const deleteReview = async (reviewId: number) => {
  // DELETES a review
  if (!token) {
    alert("You must be logged in to delete a review.");
    return;
  }
  try {
    await apiClient.delete(`/reviews/${reviewId}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    // After deleting the review from the backend, clear `userReview` from the frontend state
    setUserReview(null);
  } catch (error: any) {
    setHttpError(error.message);
  }
};
// ---- END OF FUNCTIONS ----

  // Handle loading state
  if (isLoading) {
    return <SpinnerLoading />;
  }

  // Handle HTTP errors
  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  return (
    <div>
      {/* Desktop version */}
      <div className="container d-none d-lg-block">
        <div className="row mt-5">
          <div className="col-sm-2 col-md-2">
            {coffee?.imageUrl ? (
              <img
                src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
                width="226"
                height="349"
                alt="Coffee"
              />
            ) : (
              <div>No Image Available</div>
            )}
          </div>
          <div className="col-4 col-md-4 container">
            <div className="ml-2">
              <h2>{coffee?.name}</h2>
              {coffee?.reviews && coffee.reviews.length > 0 ? (
                <div className="d-flex align-items-center mb-3">
                  <StarsDisplay rating={averageRating} />
                  <span className="ms-2 text-muted">({coffee.reviews.length} reviews)</span>
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
                <strong>Notes:</strong> {coffee?.flavorNotes}
              </p>
              <p>
                <strong>Processing Method:</strong> {coffee?.processingMethod}
              </p>
              <p>
                <strong>Production Year:</strong> {coffee?.productionYear}
              </p>
            </div>
          </div>
          {/* Review Box */}
          <ReviewBox
              coffee={coffee}
              userReview={userReview}
              createReview={createReview}
              updateReview={updateReview}
              deleteReview={deleteReview}
            />
        </div>
        <hr />
        <LatestReviews
          reviews={coffee?.reviews || []}
          coffeeId={coffee?.id}
          mobile={false}
        />
      </div>

      {/* Mobile version */}
      <div className="container d-lg-none mt-5">
        <div className="d-flex justify-content-center align-items-center">
          {coffee?.imageUrl ? (
            <img
              src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
              width="226"
              height="349"
              alt="Coffee"
            />
          ) : (
            <div>No Image Available</div>
          )}
        </div>
        <div className="mt-4">
          <div className="ml-2">
            <h2>{coffee?.name}</h2>
            {coffee?.reviews && coffee.reviews.length > 0 ? (
                <div className="d-flex align-items-center mb-3">
                  <StarsDisplay rating={averageRating} />
                  <span className="ms-2 text-muted">({coffee.reviews.length} reviews)</span>
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
              <strong>Notes:</strong> {coffee?.flavorNotes}
            </p>
            <p>
              <strong>Processing Method:</strong> {coffee?.processingMethod}
            </p>
            <p>
              <strong>Production Year:</strong> {coffee?.productionYear}
            </p>
            {/* Review Box */}
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
        <LatestReviews
          reviews={coffee?.reviews || []}
          coffeeId={coffee?.id}
          mobile={true}
        />
      </div>
    </div>
  );
};