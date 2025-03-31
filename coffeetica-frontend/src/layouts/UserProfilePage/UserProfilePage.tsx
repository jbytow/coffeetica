import { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../auth/AuthContext";
import { UserDTO } from "../../models/UserDTO";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/ui/SpinnerLoading";
import { FavouriteCoffee } from "../Utils/user/FavouriteCoffee";
import { useNavigate } from "react-router-dom";
import { ReviewDTO } from "../../models/ReviewDTO";
import { LatestReviews } from "../Utils/reviews/LatestReviews";
import { UpdateUserRequestDTO } from "../../models/UpdateUserRequestDTO";

/**
 * Displays and manages the currently authenticated user's profile information,
 * including email editing and navigation to the password change page.
 * Also shows the last three reviews by this user at the bottom,
 * handling all loading/error states locally.
 */
export const UserProfilePage = () => {
  const { isAuthenticated, updateUser, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  // Basic user info loading
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);

  const [userDetails, setUserDetails] = useState<UserDTO | null>(null);

  // Email editing
  const [email, setEmail] = useState("");
  const [isEditingEmail, setIsEditingEmail] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  // JWT token
  const token = localStorage.getItem("token");

  // For the last 3 user reviews
  const [latestReviews, setLatestReviews] = useState<ReviewDTO[]>([]);
  const [reviewsError, setReviewsError] = useState<string | null>(null);
  const [reviewsLoading, setReviewsLoading] = useState<boolean>(true);

  /**
   * 1) Fetch user data
   */
  useEffect(() => {
    if (!isAuthenticated) {
      setIsLoading(false);
      return;
    }

    const fetchUserData = async () => {
      setIsLoading(true);
      setHttpError(null);

      try {
        const response = await apiClient.get<UserDTO>("/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserDetails(response.data);
        setEmail(response.data.email);
      } catch (error: any) {
        setHttpError(error.message || "Error fetching user details.");
      } finally {
        setIsLoading(false);
      }
    };
    fetchUserData();
  }, [isAuthenticated, token]);

  /**
   * 2) Fetch last 3 reviews by this user
   */
  useEffect(() => {
    if (!userDetails) {
      setReviewsLoading(false);
      return;
    }

    const fetchLatestReviews = async () => {
      setReviewsLoading(true);
      setReviewsError(null);

      try {
        // We'll pass userId, page=0, size=3, sortBy=createdAt => newest first
        const response = await apiClient.get<{ content: ReviewDTO[] }>("/reviews", {
          params: {
            userId: userDetails.id,
            page: 0,
            size: 3,
            sortBy: "createdAt",
          },
        });
        setLatestReviews(response.data.content);
      } catch (err: any) {
        setReviewsError(err.message);
      } finally {
        setReviewsLoading(false);
      }
    };
    fetchLatestReviews();
  }, [userDetails]);

  /**
   * Handle email update
   */
  const handleUpdateEmail = async (e: React.FormEvent) => {
    e.preventDefault();
    setSuccessMsg("");
    setErrorMsg("");
  
    if (!userDetails) return;
  
    try {
      const payload: UpdateUserRequestDTO = { email };
  
      const response = await apiClient.put<UserDTO>(
        `/users/${userDetails.id}/update-email`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );
  
      setSuccessMsg("Email address has been updated. Please log in again.");
      setUserDetails(response.data);
      updateUser(response.data);
      setIsEditingEmail(false);
      
      // Wyloguj użytkownika po krótkim opóźnieniu
      setTimeout(() => {
        logout(); 
        navigate("/login", { 
          state: { 
            message: "Your email has been changed. Please log in again with your new email address." 
          } 
        });
      }, 1500); // 1.5 sekundy opóźnienia, aby użytkownik zobaczył komunikat
  
    } catch (error: any) {
      setErrorMsg("An error occurred while updating the email.");
    }
  };

  // Navigate to change password
  const handleGoToChangePassword = () => {
    navigate("/profile/change-password");
  };

  // Render logic
  if (isLoading) {
    return <SpinnerLoading />;
  }
  if (!isAuthenticated) {
    return (
      <div className="container mt-5">
        <p className="alert alert-warning">
          You must be logged in to view your profile.
        </p>
      </div>
    );
  }
  if (httpError) {
    return (
      <div className="container mt-5">
        <p className="alert alert-danger">{httpError}</p>
      </div>
    );
  }
  if (!userDetails) {
    return (
      <div className="container mt-5">
        <p className="alert alert-warning">User data could not be found.</p>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <h2 className="mb-4">My Profile</h2>

      {/* 2 columns: left user info, right favourite coffee */}
      <div className="row g-3 align-items-stretch">
        <div className="col-12 col-lg-8">
          <div className="card h-100">
            <div className="card-header">
              <h5 className="mb-0">Information</h5>
            </div>
            <div className="card-body">
              <div className="mb-3">
                <strong>Username:</strong> {userDetails.username}
              </div>

              <div className="mb-3">
                <strong>Email Address:</strong>
                {successMsg && (
                  <div className="alert alert-success mt-2">{successMsg}</div>
                )}
                {errorMsg && (
                  <div className="alert alert-danger mt-2">{errorMsg}</div>
                )}

                {!isEditingEmail ? (
                  <>
                    <div className="mt-1">{userDetails.email}</div>
                    <div className="d-flex gap-2 mt-2">
                      <button
                        className="btn btn-secondary"
                        onClick={() => setIsEditingEmail(true)}
                      >
                        Edit
                      </button>
                    </div>
                  </>
                ) : (
                  <form className="mt-2" onSubmit={handleUpdateEmail}>
                    <div className="mb-3">
                      <input
                        type="email"
                        className="form-control"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                      />
                    </div>
                    <div className="d-flex gap-2">
                      <button className="btn btn-primary" type="submit">
                        Save
                      </button>
                      <button
                        className="btn btn-outline-secondary"
                        type="button"
                        onClick={() => {
                          setEmail(userDetails.email);
                          setIsEditingEmail(false);
                        }}
                      >
                        Cancel
                      </button>
                    </div>
                  </form>
                )}
              </div>

              <hr />
              <div className="d-flex justify-content-end">
                <button
                  className="btn btn-warning"
                  onClick={handleGoToChangePassword}
                >
                  Change Password
                </button>
              </div>
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
              <FavouriteCoffee userId={userDetails.id!} />
            </div>
          </div>
        </div>
      </div>

      {/* We handle reviewsLoading/reviewsError ourselves, no 'loading/error' in LatestReviews */}
      <hr className="my-4" />
      {reviewsLoading ? (
        <SpinnerLoading />
      ) : reviewsError ? (
        <div className="alert alert-danger">{reviewsError}</div>
      ) : (
        <LatestReviews
          reviews={latestReviews}
          userId={userDetails.id}
          mobile={false}
          showCoffeeInsteadOfUser={true}
        />
      )}
    </div>
  );
};

export default UserProfilePage;