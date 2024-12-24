import React, { useState, useEffect } from "react";
import { RoasteryDTO } from "../../../models/RoasteryDTO";
import apiClient from "../../../lib/api";

const ManageRoasteries: React.FC = () => {
    const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
    const [newRoastery, setNewRoastery] = useState<Partial<RoasteryDTO>>({});
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
  
    const handleAddRoastery = async () => {
        try {
          console.log("Sending data:", newRoastery); // Sprawdź dane przed wysłaniem
          const response = await apiClient.post<RoasteryDTO>("/roasteries", newRoastery);
          console.log("Response:", response.data); // Sprawdź odpowiedź z serwera
          setRoasteries((prev) => [...prev, response.data]);
          setNewRoastery({});
        } catch (err: any) {
          console.error("Error:", err.response || err.message); // Log szczegółów błędu
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
              <label>Location:</label>
              <input
                type="text"
                placeholder="Location"
                value={newRoastery.location || ""}
                onChange={(e) =>
                  setNewRoastery({ ...newRoastery, location: e.target.value })
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
            <button type="submit">Add Roastery</button>
          </form>
        </div>
        <div>
          <h3>Existing Roasteries</h3>
          <ul>
            {roasteries.map((roastery) => (
              <li key={roastery.id}>
                <strong>{roastery.name}</strong> <br />
                Location: {roastery.location} <br />
                Founding Year: {roastery.foundingYear} <br />
                Website:{" "}
                <a href={roastery.websiteUrl} target="_blank" rel="noreferrer">
                  {roastery.websiteUrl}
                </a>{" "}
                <br />
                <button onClick={() => handleDeleteRoastery(roastery.id)}>
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