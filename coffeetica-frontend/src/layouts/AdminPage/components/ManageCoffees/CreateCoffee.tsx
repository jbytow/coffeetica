import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import apiClient from "../../../../lib/api";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import { useNavigate } from "react-router-dom";
import SearchableDropdown from "../../../Utils/SearchableDropdown";

const CreateCoffee: React.FC = () => {
  const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
  const [regions, setRegions] = useState<string[]>([]);
  const [roastLevels, setRoastLevels] = useState<string[]>([]);
  const [flavorProfiles, setFlavorProfiles] = useState<string[]>([]);
  const [newCoffee, setNewCoffee] = useState<Partial<CoffeeDTO>>({
    productionYear: new Date().getFullYear(),
    flavorNotes: [],
  });
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  // Fetch options
  useEffect(() => {
    const fetchOptions = async () => {
      try {
        const [regionsResponse, roastLevelsResponse, flavorProfilesResponse] = await Promise.all([
          apiClient.get<string[]>("/coffees/options/regions"),
          apiClient.get<string[]>("/coffees/options/roast-levels"),
          apiClient.get<string[]>("/coffees/options/flavor-profiles"),
        ]);
        setRegions(regionsResponse.data);
        setRoastLevels(roastLevelsResponse.data);
        setFlavorProfiles(flavorProfilesResponse.data);
      } catch (err: any) {
        console.error("Error fetching options:", err.response || err.message);
        setError("Failed to fetch coffee options.");
      }
    };

    fetchOptions();
  }, []);

  // Fetch roasteries
  useEffect(() => {
    const fetchRoasteries = async () => {
      try {
        const response = await apiClient.get<RoasteryDTO[]>("/roasteries");
        setRoasteries(response.data);
      } catch (err: any) {
        console.error("Error fetching roasteries:", err.response || err.message);
        setError("Failed to fetch roasteries.");
      }
    };

    fetchRoasteries();
  }, []);

  // Handle adding a new coffee
  const handleAddCoffee = async (event: React.FormEvent) => {
    event.preventDefault();

    if (
      !newCoffee.name ||
      !newCoffee.countryOfOrigin ||
      !newCoffee.region ||
      !newCoffee.roastLevel ||
      !newCoffee.flavorProfile ||
      !newCoffee.flavorNotes ||
      newCoffee.flavorNotes.length === 0 ||
      !newCoffee.processingMethod ||
      !newCoffee.productionYear ||
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
      setSuccess(true);
      setError(null);
      setTimeout(() => navigate("/admin/coffees"), 2000); // Redirect after 2 seconds
    } catch {
      setError("Failed to add coffee.");
    }
  };

  return (
    <div className="container mt-5 mb-5">
      {success && <div className="alert alert-success">Coffee added successfully!</div>}
      {error && <div className="alert alert-danger">{error}</div>}
      <div className="card">
        <div className="card-header">Add New Coffee</div>
        <div className="card-body">
          <form onSubmit={handleAddCoffee}>
            <div className="row">

              {/* Name - Flavor Profile */}
              <div className="col-md-6 mb-3">
                <label className="form-label">Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={newCoffee.name || ""}
                  onChange={(e) => setNewCoffee({ ...newCoffee, name: e.target.value })}
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={flavorProfiles}
                  label="Flavor Profile"
                  onChange={(value) => setNewCoffee({ ...newCoffee, flavorProfile: value })}
                />
              </div>

              {/* Roast Level - Roastery */}
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={roastLevels}
                  label="Roast Level"
                  onChange={(value) => setNewCoffee({ ...newCoffee, roastLevel: value })}
                />
              </div>
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={roasteries.map((roastery) => roastery.name)} // Extract roastery names
                  label="Roastery"
                  value={newCoffee.roastery?.name || ""}
                  onChange={(value) => {
                    const selectedRoastery = roasteries.find((r) => r.name === value);
                    setNewCoffee({ ...newCoffee, roastery: selectedRoastery });
                  }}
                />
              </div>

              {/* Region - Notes */}
              <div className="col-md-6 mb-3">
                <SearchableDropdown
                  options={regions}
                  label="Region"
                  onChange={(value) => setNewCoffee({ ...newCoffee, region: value })}
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Flavor Notes</label>
                <div className="form-control d-flex flex-wrap align-items-center" style={{ gap: "0.5rem", minHeight: "38px" }}>
                  {newCoffee.flavorNotes?.map((note, index) => (
                    <span key={index} className="badge bg-primary d-inline-flex align-items-center">
                      {note}
                      <button
                        type="button"
                        className="btn-close btn-close-white ms-2"
                        aria-label="Close"
                        onClick={() =>
                          setNewCoffee({
                            ...newCoffee,
                            flavorNotes: newCoffee.flavorNotes?.filter((_, i) => i !== index),
                          })
                        }
                      ></button>
                    </span>
                  ))}
                  <input
                    type="text"
                    className="border-0 flex-grow-1"
                    placeholder="Add a note and press Enter"
                    onKeyDown={(e) => {
                      if (e.key === "Enter" && e.currentTarget.value.trim()) {
                        const newNote = e.currentTarget.value.trim();
                        setNewCoffee({
                          ...newCoffee,
                          flavorNotes: [...(newCoffee.flavorNotes || []), newNote],
                        });
                        e.currentTarget.value = ""; // Clear the input
                        e.preventDefault(); // Prevent form submission
                      }
                    }}
                    style={{ outline: "none" }}
                  />
                </div>
              </div>

              {/* Country of Origin - Processing Method */}
              <div className="col-md-6 mb-3">
                <label className="form-label">Country of Origin</label>
                <input
                  type="text"
                  className="form-control"
                  value={newCoffee.countryOfOrigin || ""}
                  onChange={(e) =>
                    setNewCoffee({ ...newCoffee, countryOfOrigin: e.target.value })
                  }
                  required
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Processing Method</label>
                <input
                  type="text"
                  className="form-control"
                  value={newCoffee.processingMethod || ""}
                  onChange={(e) =>
                    setNewCoffee({ ...newCoffee, processingMethod: e.target.value })
                  }
                  required
                />
              </div>

              {/* Image - Production Year */}
              <div className="col-md-6 mb-3">
                <label className="form-label">Image</label>
                <input
                  type="file"
                  className="form-control"
                  accept="image/*"
                  onChange={(e) =>
                    setSelectedFile(e.target.files ? e.target.files[0] : null)
                  }
                />
              </div>
              <div className="col-md-6 mb-3">
                <label className="form-label">Production Year</label>
                <input
                  type="number"
                  className="form-control"
                  value={newCoffee.productionYear || ""}
                  onChange={(e) =>
                    setNewCoffee({
                      ...newCoffee,
                      productionYear: parseInt(e.target.value, 10),
                    })
                  }
                  required
                />
              </div>
            </div>
            <button type="submit" className="btn btn-primary">
              Add Coffee
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};

export default CreateCoffee;