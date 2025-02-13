import { useContext, useEffect, useState } from "react";
import { CoffeeDTO } from "../../models/CoffeeDTO";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { StarsReview } from "../Utils/StarsReview";
import { ReviewBox } from "./components/ReviewBox";
import { LatestReviews } from "./components/LatestReviews";
import { ReviewRequestDTO } from "../../models/ReviewRequestDTO";
import { ReviewDTO } from "../../models/ReviewDTO";
import { AuthContext } from "../../auth/AuthContext";
import { useParams } from "react-router-dom";


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
      if (!coffeeId || isNaN(coffeeId)) {
        console.error("Invalid coffeeId:", coffeeId);
        return;
      }
  
      console.log(`Fetching review for coffeeId: ${coffeeId}`);
  
      try {
        const response = await apiClient.get(`/reviews/user?coffeeId=${coffeeId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
  
        console.log("Response:", response);
        if (response.status === 200) {
          setUserReview(response.data);
        }
      } catch (error) {
        console.error("Error fetching user review:", error);
      }
    };
  
    fetchUserReview();
  }, [isAuthenticated, coffeeId, token]);

  const submitReview = async (reviewData: ReviewRequestDTO) => {
    if (!token) {
      alert("You must be logged in to submit a review.");
      return;
    }

    try {
      const response = await apiClient.post("/reviews", reviewData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUserReview(response.data);
    } catch (error: any) {
      setHttpError(error.message);
    }
  };

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
              <StarsReview
                rating={
                  coffee?.reviews
                    ? coffee.reviews.reduce((sum, review) => sum + review.rating, 0) /
                    coffee.reviews.length
                    : 0
                }
                size={32}
              />
            </div>
          </div>
          {/* Review Box */}
          <ReviewBox
            coffee={coffee}
            userReview={userReview}
            submitReview={submitReview}
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
            <StarsReview
              rating={
                coffee?.reviews
                  ? coffee.reviews.reduce((sum, review) => sum + review.rating, 0) /
                  coffee.reviews.length
                  : 0
              }
              size={32}
            />
            {/* Review Box */}
            <ReviewBox
              coffee={coffee}
              userReview={userReview}
              submitReview={submitReview}
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