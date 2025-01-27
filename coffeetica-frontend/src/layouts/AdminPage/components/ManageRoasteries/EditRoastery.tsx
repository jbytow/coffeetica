import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import apiClient from "../../../../lib/api";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";

const EditRoastery: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();

  const [roastery, setRoastery] = useState<Partial<RoasteryDTO>>({});
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchRoastery = async () => {
      try {
        const response = await apiClient.get<RoasteryDTO>(`/roasteries/${id}`);
        setRoastery(response.data);
      } catch (err) {
        setError("Failed to fetch roastery details.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchRoastery();
  }, [id]);

  const handleUpdateRoastery = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      await apiClient.put(`/roasteries/${id}`, roastery);

      if (selectedFile) {
        const formData = new FormData();
        formData.append("file", selectedFile);
        await apiClient.post(`/roasteries/${id}/upload-image`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }

      navigate("/admin");
    } catch {
      setError("Failed to update roastery.");
    }
  };

  if (isLoading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="container mt-5 mb-5">
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="card">
        <div className="card-header">Edit Roastery</div>
        <div className="card-body">
          <form onSubmit={handleUpdateRoastery}>
            <div className="mb-3">
              <label className="form-label">Name</label>
              <input
                type="text"
                className="form-control"
                value={roastery.name || ""}
                onChange={(e) => setRoastery({ ...roastery, name: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Country</label>
              <input
                type="text"
                className="form-control"
                value={roastery.country || ""}
                onChange={(e) => setRoastery({ ...roastery, country: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Founding Year</label>
              <input
                type="number"
                className="form-control"
                value={roastery.foundingYear || ""}
                onChange={(e) =>
                  setRoastery({ ...roastery, foundingYear: parseInt(e.target.value, 10) })
                }
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Website</label>
              <input
                type="url"
                className="form-control"
                value={roastery.websiteUrl || ""}
                onChange={(e) => setRoastery({ ...roastery, websiteUrl: e.target.value })}
                required
              />
            </div>
            <div className="mb-3">
              <label className="form-label">Change Image</label>
              <input
                type="file"
                className="form-control"
                accept="image/*"
                onChange={(e) => setSelectedFile(e.target.files ? e.target.files[0] : null)}
              />
            </div>
            <button type="submit" className="btn btn-primary">
              Update Roastery
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default EditRoastery;