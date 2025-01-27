import React, { useState } from "react";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import apiClient from "../../../../lib/api";
import { useNavigate } from "react-router-dom";

const CreateRoastery: React.FC = () => {
  const [newRoastery, setNewRoastery] = useState<Partial<RoasteryDTO>>({});
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleAddRoastery = async (event: React.FormEvent) => {
    event.preventDefault();

    if (!newRoastery.name || !newRoastery.country || !newRoastery.foundingYear || !newRoastery.websiteUrl) {
      setError("All fields are required.");
      return;
    }

    try {
      const response = await apiClient.post<RoasteryDTO>("/roasteries", newRoastery);
      const createdRoastery = response.data;

      if (selectedFile) {
        const formData = new FormData();
        formData.append("file", selectedFile);
        await apiClient.post(`/roasteries/${createdRoastery.id}/upload-image`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }

      setSuccess(true);
      setTimeout(() => navigate("/admin"), 2000);
    } catch (err) {
      setError("Failed to add roastery.");
    }
  };

  return (
    <div className="container mt-5 mb-5">
      {success && <div className="alert alert-success">Roastery added successfully!</div>}
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="card">
        <div className="card-header">Add New Roastery</div>
        <div className="card-body">
          <form onSubmit={handleAddRoastery}>
            <div className="mb-3">
              <label className="form-label">Name</label>
              <input
                type="text"
                className="form-control"
                value={newRoastery.name || ""}
                onChange={(e) => setNewRoastery({ ...newRoastery, name: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Country</label>
              <input
                type="text"
                className="form-control"
                value={newRoastery.country || ""}
                onChange={(e) => setNewRoastery({ ...newRoastery, country: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Founding Year</label>
              <input
                type="number"
                className="form-control"
                value={newRoastery.foundingYear || ""}
                onChange={(e) =>
                  setNewRoastery({ ...newRoastery, foundingYear: parseInt(e.target.value, 10) })
                }
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Website</label>
              <input
                type="url"
                className="form-control"
                value={newRoastery.websiteUrl || ""}
                onChange={(e) => setNewRoastery({ ...newRoastery, websiteUrl: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Image</label>
              <input
                type="file"
                className="form-control"
                accept="image/*"
                onChange={(e) => setSelectedFile(e.target.files ? e.target.files[0] : null)}
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Add Roastery
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateRoastery;