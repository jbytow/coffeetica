import React, { useState, useEffect } from "react";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import apiClient from "../../../../lib/api";


const ManageRoasteries: React.FC = () => {
  const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
  const [newRoastery, setNewRoastery] = useState<Partial<RoasteryDTO>>({});
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Fetch all roasteries
  useEffect(() => {
    const fetchRoasteries = async () => {
      try {
        const response = await apiClient.get<RoasteryDTO[]>("/roasteries");
        setRoasteries(response.data);
      } catch (err: any) {
        setError("Failed to fetch roasteries");
      }
    };
    fetchRoasteries();
  }, []);

  // Handle adding a new roastery
  const handleAddRoastery = async () => {
    try {
      const response = await apiClient.post<RoasteryDTO>("/roasteries", newRoastery);
      const createdRoastery = response.data;

      // If a file is selected, upload the image
      if (selectedFile) {
        const formData = new FormData();
        formData.append("file", selectedFile);
        await apiClient.post(`/roasteries/${createdRoastery.id}/upload-image`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }

      // Refresh the list of roasteries
      const updatedRoasteries = await apiClient.get<RoasteryDTO[]>("/roasteries");
      setRoasteries(updatedRoasteries.data);
      setNewRoastery({});
      setSelectedFile(null);
    } catch (err: any) {
      console.error("Error:", err.response || err.message);
      setError("Failed to add roastery");
    }
  };

  // Handle deleting a roastery
  const handleDeleteRoastery = async (id: number) => {
    try {
      await apiClient.delete(`/roasteries/${id}`);
      setRoasteries((prev) => prev.filter((roastery) => roastery.id !== id));
    } catch (err: any) {
      setError("Failed to delete roastery");
    }
  };

  return (
    <div>
      <h2>Manage Roasteries</h2>
      {error && <p className="text-danger">{error}</p>}
      <div>
        <h3>Add New Roastery</h3>
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleAddRoastery();
          }}
        >
          <div>
            <label>Name:</label>
            <input
              type="text"
              placeholder="Name"
              value={newRoastery.name || ""}
              onChange={(e) =>
                setNewRoastery({ ...newRoastery, name: e.target.value })
              }
            />
          </div>
          <div>
            <label>Country:</label>
            <input
              type="text"
              placeholder="Country"
              value={newRoastery.country || ""}
              onChange={(e) =>
                setNewRoastery({ ...newRoastery, country: e.target.value })
              }
            />
          </div>
          <div>
            <label>Founding Year:</label>
            <input
              type="number"
              placeholder="Founding Year"
              value={newRoastery.foundingYear || ""}
              onChange={(e) =>
                setNewRoastery({
                  ...newRoastery,
                  foundingYear: parseInt(e.target.value, 10),
                })
              }
            />
          </div>
          <div>
            <label>Website URL:</label>
            <input
              type="url"
              placeholder="Website URL"
              value={newRoastery.websiteUrl || ""}
              onChange={(e) =>
                setNewRoastery({ ...newRoastery, websiteUrl: e.target.value })
              }
            />
          </div>
          <div>
            <label>Image:</label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) =>
                setSelectedFile(e.target.files ? e.target.files[0] : null)
              }
            />
          </div>
          <button type="submit" className="btn btn-primary">Add Roastery</button>
        </form>
      </div>
      <div>
        <h3>Existing Roasteries</h3>
        <ul>
          {roasteries.map((roastery) => (
            <li key={roastery.id}>
              <strong>{roastery.name}</strong> <br />
              Country: {roastery.country} <br />
              Founding Year: {roastery.foundingYear} <br />
              Website:{" "}
              <a href={roastery.websiteUrl} target="_blank" rel="noreferrer">
                {roastery.websiteUrl}
              </a>{" "}
              <br />
              {roastery.imageUrl && (
                <img
                  src={`http://localhost:8080${roastery.imageUrl}`}
                  alt={`${roastery.name} image`}
                  width="100"
                />
              )}
              <button className="btn btn-danger mt-2" onClick={() => handleDeleteRoastery(roastery.id)}>
                Delete
              </button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default ManageRoasteries;