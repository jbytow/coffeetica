import { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../auth/AuthContext";
import { UserDTO } from "../../models/UserDTO";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { FavouriteCoffee } from "../Utils/FavouriteCoffee";
import { useNavigate } from "react-router-dom";

/**
 * Displays and manages the currently authenticated user's profile information,
 * including email editing and navigation to the password change page.
 */
export const UserProfilePage = () => {
  const { isAuthenticated, updateUser } = useContext(AuthContext);
  const navigate = useNavigate();

  // Loading/error states for retrieving user data
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);

  // Holds the user's data
  const [userDetails, setUserDetails] = useState<UserDTO | null>(null);

  // Email editing states
  const [email, setEmail] = useState("");
  const [isEditingEmail, setIsEditingEmail] = useState(false);

  // Feedback messages
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  // JWT token for authenticated requests
  const token = localStorage.getItem("token");

  /**
   * Fetches the current user's data from the backend.
   * If not authenticated, stops loading immediately.
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
   * Handles the email update request to the backend.
   */
  const handleUpdateEmail = async (e: React.FormEvent) => {
    e.preventDefault();
    setSuccessMsg("");
    setErrorMsg("");

    if (!userDetails) return;

    try {
      const payload: Partial<UserDTO> = {
        id: userDetails.id,
        username: userDetails.username,
        email: email,
        roles: userDetails.roles,
      };

      const response = await apiClient.put<UserDTO>(
        `/users/${userDetails.id}`,
        payload,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      setSuccessMsg("Email address has been updated.");
      setUserDetails(response.data);
      updateUser(response.data);
      setIsEditingEmail(false);
    } catch (error: any) {
      setErrorMsg("An error occurred while updating the email.");
    }
  };

  /**
   * Navigates to the separate page where the user can change their password.
   */
  const handleGoToChangePassword = () => {
    navigate("/profile/change-password");
  };

  // Render conditions
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
        <p className="alert alert-warning">
          User data could not be found.
        </p>
      </div>
    );
  }

  // Main layout
  return (
    <div className="container mt-5">
      <h2 className="mb-4">My Profile</h2>

      {/* Two columns (8:4). align-items-stretch ensures the cards share the same height */}
      <div className="row g-3 align-items-stretch">

        {/* Left column: user information */}
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
                  <>
                    {successMsg && (
                      <div className="alert alert-success mt-2">
                        {successMsg}
                      </div>
                    )}
                    {errorMsg && (
                      <div className="alert alert-danger mt-2">
                        {errorMsg}
                      </div>
                    )}
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
                  </>
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
    </div>
  );
};

export default UserProfilePage;