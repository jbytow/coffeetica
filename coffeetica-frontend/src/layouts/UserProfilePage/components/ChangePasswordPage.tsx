import { useContext, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../../auth/AuthContext";
import apiClient from "../../../lib/api";
import { ChangePasswordRequestDTO } from "../../../models/ChangePasswordRequestDTO";


/**
 * Page/form for changing the authenticated user's password.
 * Requires an endpoint:
 *    PUT /api/users/{id}/change-password
 * which accepts an object { currentPassword, newPassword }.
 */
export const ChangePasswordPage: React.FC = () => {
  const { isAuthenticated, user } = useContext(AuthContext);
  const navigate = useNavigate();

  // Form fields for password change
  const [currentPassword, setCurrentPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmNewPassword, setConfirmNewPassword] = useState("");

  // Feedback messages
  const [errorMsg, setErrorMsg] = useState("");
  const [successMsg, setSuccessMsg] = useState("");

  // Loading state for the request
  const [isLoading, setIsLoading] = useState(false);

  // JWT token
  const token = localStorage.getItem("token");

  // Only authenticated users can access this page
  if (!isAuthenticated || !user) {
    return (
      <div className="container mt-5">
        <p className="alert alert-warning">
          You must be logged in to change your password.
        </p>
      </div>
    );
  }

  /**
   * Handles submission of the password change form.
   * Validates new password confirmation and calls the backend endpoint.
   */
  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg("");
    setSuccessMsg("");

    // Basic validation: newPassword must match confirmNewPassword
    if (newPassword !== confirmNewPassword) {
      setErrorMsg("New password and confirmation do not match.");
      return;
    }

    setIsLoading(true);

    try {
      const payload: ChangePasswordRequestDTO = { currentPassword, newPassword };
  
      await apiClient.put(`/users/${user.id}/change-password`, payload, {
        headers: { Authorization: `Bearer ${token}` },
      });

      setSuccessMsg("Password has been changed successfully.");
      // Clear form fields
      setCurrentPassword("");
      setNewPassword("");
      setConfirmNewPassword("");
    } catch (error: any) {
      setErrorMsg(
        error?.response?.data || "An error occurred while changing the password."
      );
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Navigates back to the user profile page.
   */
  const handleBackToProfile = () => {
    navigate("/profile");
  };

  return (
    <div className="container my-5" style={{ maxWidth: "600px" }}>
      <h3>Change Password</h3>
      <div className="card mt-4">
        <div className="card-body">
          {successMsg && <div className="alert alert-success">{successMsg}</div>}
          {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}

          <form onSubmit={handleChangePassword}>
            {/* Current password */}
            <div className="mb-3">
              <label htmlFor="currentPassword" className="form-label">
                Current Password
              </label>
              <input
                type="password"
                id="currentPassword"
                className="form-control"
                value={currentPassword}
                onChange={(e) => setCurrentPassword(e.target.value)}
                required
              />
            </div>

            {/* New password */}
            <div className="mb-3">
              <label htmlFor="newPassword" className="form-label">
                New Password
              </label>
              <input
                type="password"
                id="newPassword"
                className="form-control"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
              />
            </div>

            {/* Confirm new password */}
            <div className="mb-3">
              <label htmlFor="confirmNewPassword" className="form-label">
                Confirm New Password
              </label>
              <input
                type="password"
                id="confirmNewPassword"
                className="form-control"
                value={confirmNewPassword}
                onChange={(e) => setConfirmNewPassword(e.target.value)}
                required
              />
            </div>

            <button
              type="submit"
              className="btn btn-primary"
              disabled={isLoading}
            >
              {isLoading ? "Saving..." : "Change Password"}
            </button>

            <button
              type="button"
              className="btn btn-secondary ms-2"
              onClick={handleBackToProfile}
            >
              Back
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ChangePasswordPage;