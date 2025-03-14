import { useContext, useEffect, useState } from "react";
import { AuthContext } from "../../../../auth/AuthContext";
import apiClient from "../../../../lib/api";
import { SpinnerLoading } from "../../../Utils/ui/SpinnerLoading";
import { UserDTO } from "../../../../models/UserDTO";
import { Pagination } from "../../../Utils/ui/Pagination";
import { AdminUpdateUserRequestDTO } from "../../../../models/AdminUpdateUserRequestDTO";
import { ResetPasswordRequestDTO } from "../../../../models/ResetPasswordRequestDTO";
import { UpdateRoleRequestDTO } from "../../../../models/UpdateRoleRequestDTO";

interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  number: number; // current page (0-based)
  size: number;   // page size
}

/**
 * Manages the user list with pagination, search, and various pop-up modals
 * for editing user details, password, roles, and deleting.
 */
const ManageUsers: React.FC = () => {
  const { hasRole } = useContext(AuthContext);

  // Search / fetch states
  const [searchTerm, setSearchTerm] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [httpError, setHttpError] = useState<string | null>(null);

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  // List of users
  const [users, setUsers] = useState<UserDTO[]>([]);

  // --- Different modals we might open. ---
  const [editUserModal, setEditUserModal] = useState<UserDTO | null>(null);   
  const [editPasswordModal, setEditPasswordModal] = useState<UserDTO | null>(null);
  const [deleteUserModal, setDeleteUserModal] = useState<UserDTO | null>(null);
  const [editRolesModal, setEditRolesModal] = useState<UserDTO | null>(null);

  // For the edit user modal
  const [editName, setEditName] = useState("");
  const [editEmail, setEditEmail] = useState("");

  // For roles
  const [tempRoles, setTempRoles] = useState<string[]>([]);

  // For password
  const [newPassword, setNewPassword] = useState("");

  // -------------- Helper to fetch users with pagination --------------
  const fetchUsers = async () => {
    setIsLoading(true);
    setHttpError(null);
    try {
      const response = await apiClient.get<Page<UserDTO>>("/users", {
        params: {
          search: searchTerm,
          page: currentPage - 1, // 0-based on server
          size: 10,
          sortBy: "username",
          direction: "asc",
        },
      });
      setUsers(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error: any) {
      setHttpError(error.message);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage]);

  // -------------- Searching resets to page=1 --------------
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setCurrentPage(1);
    fetchUsers();
  };

  // -------------- Pagination --------------
  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  // -------------- Front-end safeguard --------------
  const isProtectedUser = (u: UserDTO) => {
    const rolesSet = new Set(u.roles.map((r) => r.toLowerCase()));
    return rolesSet.has("admin") || rolesSet.has("superadmin");
  };

  // -------------- Edit User (Name + Email) --------------
  const openEditUserModal = (u: UserDTO) => {
    if (!hasRole("SuperAdmin") && hasRole("Admin") && isProtectedUser(u)) {
      alert("You cannot edit an Admin/SuperAdmin user.");
      return;
    }
    setEditUserModal(u);
    setEditName(u.username);
    setEditEmail(u.email);
  };
  const closeEditUserModal = () => {
    setEditUserModal(null);
    setEditName("");
    setEditEmail("");
  };
  const saveEditUser = async () => {
    if (!editUserModal) return;
    try {
      const payload: AdminUpdateUserRequestDTO = {
        username: editName,
        email: editEmail,
      };
      await apiClient.put(`/users/${editUserModal.id}`, payload);
      fetchUsers();
      closeEditUserModal();
    } catch (err: any) {
      alert("Error saving user: " + err.message);
    }
  };

  // -------------- Edit Password --------------
  const openEditPasswordModal = (u: UserDTO) => {
    if (!hasRole("SuperAdmin") && hasRole("Admin") && isProtectedUser(u)) {
      alert("You cannot reset the password of an Admin/SuperAdmin user.");
      return;
    }
    setEditPasswordModal(u);
    setNewPassword("");
  };
  const closeEditPasswordModal = () => {
    setEditPasswordModal(null);
    setNewPassword("");
  };
  const saveEditPassword = async () => {
    if (!editPasswordModal) return;
    try {
      const payload: ResetPasswordRequestDTO = {
        newPassword,
      };
      await apiClient.put(`/users/${editPasswordModal.id}/reset-password`, payload);
      fetchUsers();
      closeEditPasswordModal();
    } catch (err: any) {
      alert("Error changing password: " + err.message);
    }
  };

  // -------------- Delete User --------------
  const openDeleteUserModal = (u: UserDTO) => {
    if (!hasRole("SuperAdmin") && hasRole("Admin") && isProtectedUser(u)) {
      alert("You cannot delete an Admin/SuperAdmin user.");
      return;
    }
    setDeleteUserModal(u);
  };
  const closeDeleteUserModal = () => {
    setDeleteUserModal(null);
  };
  const confirmDeleteUser = async () => {
    if (!deleteUserModal) return;
    try {
      await apiClient.delete(`/users/${deleteUserModal.id}`);
      fetchUsers();
      closeDeleteUserModal();
    } catch (err: any) {
      alert("Error deleting user: " + err.message);
    }
  };

  // -------------- Edit Roles (SuperAdmin only) --------------
  const openEditRolesModal = (u: UserDTO) => {
    if (!hasRole("SuperAdmin")) {
      return;
    }
    setEditRolesModal(u);
    setTempRoles([...u.roles]);
  };
  const closeEditRolesModal = () => {
    setEditRolesModal(null);
    setTempRoles([]);
  };
  const toggleOneRole = (roleName: string) => {
    setTempRoles((prev) => {
      if (prev.includes(roleName)) {
        return prev.filter((r) => r !== roleName);
      }
      return [...prev, roleName];
    });
  };
  const saveEditRoles = async () => {
    if (!editRolesModal) return;
    try {
      const payload: UpdateRoleRequestDTO = {
        roles: tempRoles,
      };
      await apiClient.put(`/users/${editRolesModal.id}/update-roles`, payload);
      fetchUsers();
      closeEditRolesModal();
    } catch (err: any) {
      alert("Error updating roles: " + err.message);
    }
  };

  // -------------- RENDER --------------
  if (isLoading) {
    return <SpinnerLoading />;
  }
  if (httpError) {
    return <div className="alert alert-danger">Error: {httpError}</div>;
  }

  return (
    <div className="manage-users">
      <h3>Manage Users</h3>

      {/* Search bar */}
      <form className="d-flex mb-3" onSubmit={handleSearch}>
        <input
          type="text"
          className="form-control me-2"
          placeholder="Filter by username or email"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button className="btn btn-outline-primary" type="submit">
          Search
        </button>
      </form>

      {/* Table of users */}
      {users.length === 0 ? (
        <p>No users found.</p>
      ) : (
        <table className="table table-striped">
          <thead>
            <tr>
              <th>Username</th>
              <th>Email</th>
              <th>Roles</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id}>
                <td>{u.username}</td>
                <td>{u.email}</td>
                <td>{u.roles.join(", ")}</td>
                <td>
                  {/* Edit user (name/email) */}
                  <button
                    className="btn btn-sm btn-secondary me-2"
                    onClick={() => openEditUserModal(u)}
                  >
                    Edit
                  </button>

                  {/* Edit password */}
                  <button
                    className="btn btn-sm btn-warning me-2"
                    onClick={() => openEditPasswordModal(u)}
                  >
                    Edit Password
                  </button>

                  {/* Edit Roles (only visible if superadmin) */}
                  {hasRole("SuperAdmin") && (
                    <button
                      className="btn btn-sm btn-info me-2"
                      onClick={() => openEditRolesModal(u)}
                    >
                      Edit Roles
                    </button>
                  )}

                  {/* Delete */}
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => openDeleteUserModal(u)}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          paginate={paginate}
        />
      )}

      {/* -- EDIT USER MODAL -- */}
      {editUserModal && (
        <>
          {/* Backdrop */}
          <div className="modal-backdrop fade show"></div>
          <div
            className="modal fade show"
            style={{ display: "block" }}
            tabIndex={-1}
          >
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">Edit User: {editUserModal.username}</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={closeEditUserModal}
                  ></button>
                </div>
                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">Username</label>
                    <input
                      type="text"
                      className="form-control"
                      value={editName}
                      onChange={(e) => setEditName(e.target.value)}
                    />
                  </div>
                  <div className="mb-3">
                    <label className="form-label">Email</label>
                    <input
                      type="email"
                      className="form-control"
                      value={editEmail}
                      onChange={(e) => setEditEmail(e.target.value)}
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button className="btn btn-secondary" onClick={closeEditUserModal}>
                    Cancel
                  </button>
                  <button className="btn btn-primary" onClick={saveEditUser}>
                    Save
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}

      {/* -- EDIT PASSWORD MODAL -- */}
      {editPasswordModal && (
        <>
          <div className="modal-backdrop fade show"></div>
          <div
            className="modal fade show"
            style={{ display: "block" }}
            tabIndex={-1}
          >
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">
                    Change Password for {editPasswordModal.username}
                  </h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={closeEditPasswordModal}
                  ></button>
                </div>
                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">New Password</label>
                    <input
                      type="password"
                      className="form-control"
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                    />
                  </div>
                </div>
                <div className="modal-footer">
                  <button className="btn btn-secondary" onClick={closeEditPasswordModal}>
                    Cancel
                  </button>
                  <button className="btn btn-primary" onClick={saveEditPassword}>
                    Save
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}

      {/* -- DELETE USER MODAL -- */}
      {deleteUserModal && (
        <>
          <div className="modal-backdrop fade show"></div>
          <div
            className="modal fade show"
            style={{ display: "block" }}
            tabIndex={-1}
          >
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">
                    Delete User: {deleteUserModal.username}
                  </h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={closeDeleteUserModal}
                  ></button>
                </div>
                <div className="modal-body">
                  <p>Are you sure you want to delete this user?</p>
                </div>
                <div className="modal-footer">
                  <button className="btn btn-secondary" onClick={closeDeleteUserModal}>
                    Cancel
                  </button>
                  <button className="btn btn-danger" onClick={confirmDeleteUser}>
                    Delete
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}

      {/* -- EDIT ROLES MODAL -- (SuperAdmin only) */}
      {editRolesModal && (
        <>
          <div className="modal-backdrop fade show"></div>
          <div
            className="modal fade show"
            style={{ display: "block" }}
            tabIndex={-1}
          >
            <div className="modal-dialog modal-dialog-centered">
              <div className="modal-content">
                <div className="modal-header">
                  <h5 className="modal-title">
                    Edit Roles for {editRolesModal.username}
                  </h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={closeEditRolesModal}
                  ></button>
                </div>
                <div className="modal-body">
                  {["User", "Admin", "SuperAdmin"].map((roleName) => (
                    <div className="form-check" key={roleName}>
                      <input
                        className="form-check-input"
                        type="checkbox"
                        id={`role-${roleName}`}
                        checked={tempRoles.includes(roleName)}
                        onChange={() => toggleOneRole(roleName)}
                      />
                      <label className="form-check-label" htmlFor={`role-${roleName}`}>
                        {roleName}
                      </label>
                    </div>
                  ))}
                </div>
                <div className="modal-footer">
                  <button className="btn btn-secondary" onClick={closeEditRolesModal}>
                    Cancel
                  </button>
                  <button className="btn btn-primary" onClick={saveEditRoles}>
                    Save
                  </button>
                </div>
              </div>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default ManageUsers;