import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import apiClient from "../../../../lib/api";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import { useNavigate } from "react-router-dom";

const CreateCoffee: React.FC = () => {
    const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
    const [newCoffee, setNewCoffee] = useState<Partial<CoffeeDTO>>({});
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();
  
    // Fetch roasteries for the dropdown
    useEffect(() => {
      const fetchRoasteries = async () => {
        try {
          const response = await apiClient.get<RoasteryDTO[]>("/roasteries");
          setRoasteries(response.data);
        } catch (err: any) {
          console.error("Error fetching roasteries:", err.response || err.message);
          setError("Failed to fetch roasteries");
        }
      };
  
      fetchRoasteries();
    }, []);
  
    // Handle adding a new coffee
    const handleAddCoffee = async () => {
      if (
        !newCoffee.name ||
        !newCoffee.countryOfOrigin ||
        !newCoffee.roastLevel ||
        !newCoffee.flavorProfile ||
        !newCoffee.roastery
      ) {
        setError("All fields, including roastery, are required.");
        return;
      }
  
      try {
        const response = await apiClient.post<CoffeeDTO>("/coffees", newCoffee);
        const createdCoffee = response.data;
  
        // If a file is selected, upload the image
        if (selectedFile) {
          const formData = new FormData();
          formData.append("file", selectedFile);
          await apiClient.post(`/coffees/${createdCoffee.id}/upload-image`, formData, {
            headers: { "Content-Type": "multipart/form-data" },
          });
        }
  
        // Navigate back to the coffee list or admin panel
        navigate("/admin/coffees");
      } catch (err: any) {
        console.error("Error adding coffee:", err.response || err.message);
        setError("Failed to add coffee");
      }
    };
  
    return (
      <div>
        <h2>Add New Coffee</h2>
        {error && <p className="text-danger">{error}</p>}
        <form
          onSubmit={(e) => {
            e.preventDefault();
            handleAddCoffee();
          }}
        >
          <div>
            <label>Name:</label>
            <input
              type="text"
              placeholder="Name"
              value={newCoffee.name || ""}
              onChange={(e) => setNewCoffee({ ...newCoffee, name: e.target.value })}
            />
          </div>
          <div>
            <label>Country of Origin:</label>
            <input
              type="text"
              placeholder="Country of Origin"
              value={newCoffee.countryOfOrigin || ""}
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, countryOfOrigin: e.target.value })
              }
            />
          </div>
          <div>
            <label>Region:</label>
            <input
              type="text"
              placeholder="Region"
              value={newCoffee.region || ""}
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, region: e.target.value })
              }
            />
          </div>
          <div>
            <label>Roast Level:</label>
            <input
              type="text"
              placeholder="Roast Level"
              value={newCoffee.roastLevel || ""}
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, roastLevel: e.target.value })
              }
            />
          </div>
          <div>
            <label>Flavor Profile:</label>
            <input
              type="text"
              placeholder="Flavor Profile"
              value={newCoffee.flavorProfile || ""}
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, flavorProfile: e.target.value })
              }
            />
          </div>
          <div>
            <label>Notes:</label>
            <input
              type="text"
              placeholder="Notes"
              value={newCoffee.notes || ""}
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, notes: e.target.value })
              }
            />
          </div>
          <div>
            <label>Processing Method:</label>
            <input
              type="text"
              placeholder="Processing Method"
              value={newCoffee.processingMethod || ""}
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, processingMethod: e.target.value })
              }
            />
          </div>
          <div>
            <label>Production Year:</label>
            <input
              type="number"
              placeholder="Production Year"
              value={newCoffee.productionYear || ""}
              onChange={(e) =>
                setNewCoffee({
                  ...newCoffee,
                  productionYear: parseInt(e.target.value, 10),
                })
              }
            />
          </div>
          <div>
            <label>Roastery:</label>
            <select
              value={newCoffee.roastery?.id || ""}
              onChange={(e) => {
                const roasteryId = parseInt(e.target.value, 10);
                const selectedRoastery = roasteries.find(
                  (roastery) => roastery.id === roasteryId
                );
                setNewCoffee({ ...newCoffee, roastery: selectedRoastery });
              }}
            >
              <option value="">Select a Roastery</option>
              {roasteries.map((roastery) => (
                <option key={roastery.id} value={roastery.id}>
                  {roastery.name}
                </option>
              ))}
            </select>
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
          <button type="submit" className="btn btn-primary mt-3">
            Add Coffee
          </button>
        </form>
      </div>
    );
  };
  
  export default CreateCoffee;