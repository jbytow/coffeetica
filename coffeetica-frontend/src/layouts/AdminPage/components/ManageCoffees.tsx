import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import apiClient from "../../../lib/api";
import { RoasteryDTO } from "../../../models/RoasteryDTO";

const ManageCoffees: React.FC = () => {
  const [coffees, setCoffees] = useState<CoffeeDTO[]>([]);
  const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
  const [newCoffee, setNewCoffee] = useState<Partial<CoffeeDTO>>({});
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);

  // Fetch all coffees and roasteries
  useEffect(() => {
    const fetchCoffees = async () => {
      try {
        const response = await apiClient.get<CoffeeDTO[]>("/coffees");
        setCoffees(response.data);
      } catch (err: any) {
        console.error("Error fetching coffees:", err.response || err.message);
        setError("Failed to fetch coffees");
      }
    };

    const fetchRoasteries = async () => {
      try {
        const response = await apiClient.get<RoasteryDTO[]>("/roasteries");
        setRoasteries(response.data);
      } catch (err: any) {
        console.error("Error fetching roasteries:", err.response || err.message);
        setError("Failed to fetch roasteries");
      }
    };

    fetchCoffees();
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

      // Refresh the list of coffees
      const updatedCoffees = await apiClient.get<CoffeeDTO[]>("/coffees");
      setCoffees(updatedCoffees.data);
      setNewCoffee({});
      setSelectedFile(null);
      setError(null);
    } catch (err: any) {
      console.error("Error adding coffee:", err.response || err.message);
      setError("Failed to add coffee");
    }
  };

  // Handle deleting a coffee
  const handleDeleteCoffee = async (id: number) => {
    try {
      await apiClient.delete(`/coffees/${id}`);
      setCoffees((prev) => prev.filter((coffee) => coffee.id !== id));
    } catch (err: any) {
      console.error("Error deleting coffee:", err.response || err.message);
      setError("Failed to delete coffee");
    }
  };

  return (
    <div>
      <h2>Manage Coffees</h2>
      {error && <p className="text-danger">{error}</p>}
      <div>
        <h3>Add New Coffee</h3>
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
              onChange={(e) =>
                setNewCoffee({ ...newCoffee, name: e.target.value })
              }
            />
          </div>
          <div>
            <label>Country of Origin:</label>
            <input
              type="text"
              placeholder="Country of Origin"
              value={newCoffee.countryOfOrigin || ""}
              onChange={(e) =>
                setNewCoffee({
                  ...newCoffee,
                  countryOfOrigin: e.target.value,
                })
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
                setNewCoffee({
                  ...newCoffee,
                  region: e.target.value,
                })
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
                setNewCoffee({
                  ...newCoffee,
                  flavorProfile: e.target.value,
                })
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
                setNewCoffee({
                  ...newCoffee,
                  processingMethod: e.target.value,
                })
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
          <button type="submit" className="btn btn-primary">Add Coffee</button>
        </form>
      </div>
      <div>
        <h3>Existing Coffees</h3>
        <ul>
          {coffees.map((coffee) => (
            <li key={coffee.id}>
              <strong>{coffee.name}</strong> <br />
              Country: {coffee.countryOfOrigin} <br />
              Region: {coffee.region} <br />
              Roast Level: {coffee.roastLevel} <br />
              Flavor Profile: {coffee.flavorProfile} <br />
              Notes: {coffee.notes} <br />
              Processing Method: {coffee.processingMethod} <br />
              Production Year: {coffee.productionYear} <br />
              Roastery: {coffee.roastery?.name || "Unknown"} <br />
              {coffee.imageUrl && (
                <img
                  src={`http://localhost:8080${coffee.imageUrl}`}
                  alt={`${coffee.name} image`}
                  width="100"
                />
              )}
              <button className="btn btn-danger" onClick={() => handleDeleteCoffee(coffee.id)}>Delete</button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default ManageCoffees;