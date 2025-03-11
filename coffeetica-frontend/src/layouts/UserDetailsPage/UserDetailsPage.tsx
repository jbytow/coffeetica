import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { UserDTO } from "../../models/UserDTO";
import { ReviewDTO } from "../../models/ReviewDTO";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/ui/SpinnerLoading";
import { FavouriteCoffee } from "../Utils/user/FavouriteCoffee";
import { LatestReviews } from "../Utils/reviews/LatestReviews";

/**
 * A read-only page that displays another user's details by ID, plus
 * their favorite coffee and the last 3 reviews they posted.
 * No editing or password change options are provided.
 */
export const UserDetailsPage = () => {
    const { id } = useParams<{ id: string }>();
  
    // States for fetching the user data
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);
    const [userData, setUserData] = useState<UserDTO | null>(null);
  
    // States for fetching the user’s last 3 reviews
    const [reviewsLoading, setReviewsLoading] = useState(true);
    const [reviewsError, setReviewsError] = useState<string | null>(null);
    const [latestReviews, setLatestReviews] = useState<ReviewDTO[]>([]);
  
    /**
     * 1) Fetch user details from /api/users/{id}
     */
    useEffect(() => {
      if (!id) {
        setIsLoading(false);
        return;
      }
  
      const fetchUserById = async () => {
        setIsLoading(true);
        setHttpError(null);
  
        try {
          // For example: GET /api/users/{id}
          const response = await apiClient.get<UserDTO>(`/users/${id}`);
          setUserData(response.data);
        } catch (error: any) {
          setHttpError(error.message || "Error fetching user details.");
        } finally {
          setIsLoading(false);
        }
      };
      fetchUserById();
    }, [id]);
  
    /**
     * 2) Fetch the user’s last 3 reviews, passing userId to /api/reviews
     */
    useEffect(() => {
      if (!userData) {
        setReviewsLoading(false);
        return;
      }
  
      const fetchLatestReviews = async () => {
        setReviewsLoading(true);
        setReviewsError(null);
  
        try {
          // GET /reviews?userId=xxx&page=0&size=3&sortBy=createdAt
          const resp = await apiClient.get<{ content: ReviewDTO[] }>("/reviews", {
            params: {
              userId: userData.id,
              page: 0,
              size: 3,
              sortBy: "createdAt",
            },
          });
          setLatestReviews(resp.data.content);
        } catch (err: any) {
          setReviewsError(err.message);
        } finally {
          setReviewsLoading(false);
        }
      };
      fetchLatestReviews();
    }, [userData]);
  
    // Basic loading/error for user data
    if (isLoading) {
      return <SpinnerLoading />;
    }
    if (httpError) {
      return (
        <div className="container mt-5">
          <p className="alert alert-danger">{httpError}</p>
        </div>
      );
    }
    if (!userData) {
      return (
        <div className="container mt-5">
          <p className="alert alert-warning">User not found.</p>
        </div>
      );
    }
  
    return (
      <div className="container mt-5">
        <h2 className="mb-4">User Details</h2>
  
        {/* 2 columns: left user info, right favourite coffee */}
        <div className="row g-3 align-items-stretch">
          {/* Left column: read-only user info */}
          <div className="col-12 col-lg-8">
            <div className="card h-100">
              <div className="card-header">
                <h5 className="mb-0">Information</h5>
              </div>
              <div className="card-body">
                <div className="mb-3">
                  <strong>Username:</strong> {userData.username}
                </div>
                <div className="mb-3">
                  <strong>Email Address:</strong> {userData.email}
                </div>
                {/* Possibly some roles, etc. – if you want. Just read-only. */}
              </div>
            </div>
          </div>
  
          {/* Right column: favourite coffee */}
          <div className="col-12 col-lg-4">
            <div className="card h-100">
              <div className="card-header">
                <h5 className="mb-0">Favourite Coffee</h5>
              </div>
              <div className="card-body">
                {/* Show the user’s favorite coffee (rating=5) exactly like user profile */}
                <FavouriteCoffee userId={userData.id!} />
              </div>
            </div>
          </div>
        </div>
  
        {/* Show last 3 reviews from that user, in the same style as user profile. */}
        <hr className="my-4" />
        {reviewsLoading ? (
          <SpinnerLoading />
        ) : reviewsError ? (
          <div className="alert alert-danger">{reviewsError}</div>
        ) : (
          // This is a "latest reviews" section, but we pass showCoffeeInsteadOfUser
          // so each review displays coffee name
          <LatestReviews
            reviews={latestReviews}
            userId={userData.id}
            mobile={false}
            showCoffeeInsteadOfUser={true}
          />
        )}
      </div>
    );
  };
  
  export default UserDetailsPage;