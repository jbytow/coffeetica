import React, { useState, useEffect } from "react";
import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import apiClient from "../../../../lib/api";
import { RoasteryDTO } from "../../../../models/RoasteryDTO";
import { useNavigate } from "react-router-dom";

const CreateCoffee: React.FC = () => {
    const [roasteries, setRoasteries] = useState<RoasteryDTO[]>([]);
    const [regions, setRegions] = useState<string[]>([]);
    const [roastLevels, setRoastLevels] = useState<string[]>([]);
    const [flavorProfiles, setFlavorProfiles] = useState<string[]>([]);
    const [newCoffee, setNewCoffee] = useState<Partial<CoffeeDTO>>({
        productionYear: new Date().getFullYear(),
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
            !newCoffee.notes ||
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
                                <label className="form-label">Flavor Profile</label>
                                <select
                                    className="form-select"
                                    value={newCoffee.flavorProfile || ""}
                                    onChange={(e) =>
                                        setNewCoffee({ ...newCoffee, flavorProfile: e.target.value })
                                    }
                                    required
                                >
                                    <option value="">Select a flavor profile</option>
                                    {flavorProfiles.map((profile) => (
                                        <option key={profile} value={profile}>
                                            {profile}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Roastery - Notes */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Roastery</label>
                                <select
                                    className="form-select"
                                    value={newCoffee.roastery?.id || ""}
                                    onChange={(e) => {
                                        const roasteryId = parseInt(e.target.value, 10);
                                        const selectedRoastery = roasteries.find((r) => r.id === roasteryId);
                                        setNewCoffee({ ...newCoffee, roastery: selectedRoastery });
                                    }}
                                    required
                                >
                                    <option value="">Select a roastery</option>
                                    {roasteries.map((roastery) => (
                                        <option key={roastery.id} value={roastery.id}>
                                            {roastery.name}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Notes</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    value={newCoffee.notes || ""}
                                    onChange={(e) => setNewCoffee({ ...newCoffee, notes: e.target.value })}
                                    required
                                />
                            </div>

                            {/* Region - Roast Level */}
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Region</label>
                                <select
                                    className="form-select"
                                    value={newCoffee.region || ""}
                                    onChange={(e) => setNewCoffee({ ...newCoffee, region: e.target.value })}
                                    required
                                >
                                    <option value="">Select a region</option>
                                    {regions.map((region) => (
                                        <option key={region} value={region}>
                                            {region}
                                        </option>
                                    ))}
                                </select>
                            </div>
                            <div className="col-md-6 mb-3">
                                <label className="form-label">Roast Level</label>
                                <select
                                    className="form-select"
                                    value={newCoffee.roastLevel || ""}
                                    onChange={(e) => setNewCoffee({ ...newCoffee, roastLevel: e.target.value })}
                                    required
                                >
                                    <option value="">Select a roast level</option>
                                    {roastLevels.map((level) => (
                                        <option key={level} value={level}>
                                            {level}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            {/* Country - Processing Method */}
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