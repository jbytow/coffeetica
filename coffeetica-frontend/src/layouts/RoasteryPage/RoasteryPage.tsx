import { useEffect, useState } from "react";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/ui/SpinnerLoading";
import { useParams } from "react-router-dom";
import { RoasteryDTO } from "../../models/RoasteryDTO";
import { CoffeeDTO } from "../../models/CoffeeDTO";
import { FeaturedCoffee } from "./components/FeaturedCoffee";
import { LatestCoffees } from "./components/LatestCoffees";

/**
 * Displays roastery details along with its featured coffee and latest added coffees.
 */
export const RoasteryPage = () => {
  const [roastery, setRoastery] = useState<RoasteryDTO | undefined>();
  const [latestCoffees, setLatestCoffees] = useState<CoffeeDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);

  // Extract roasteryId from URL parameters
  const { id } = useParams<{ id: string }>();
  const roasteryId = id ? Number(id) : null;

  useEffect(() => {
    const fetchData = async () => {
      if (!roasteryId) {
        setIsLoading(false);
        return;
      }

      try {
        // Fetch roastery details from the backend
        const roasteryResponse = await apiClient.get<RoasteryDTO>(
          `/roasteries/${roasteryId}`
        );
        setRoastery(roasteryResponse.data);

        // Fetch latest coffees for the roastery (first 3 items)
        // The backend returns a Page<CoffeeDTO> so we use the content field
        const coffeesResponse = await apiClient.get<{ content: CoffeeDTO[] }>(
          `/roasteries/${roasteryId}/coffees?page=0&size=3&sortBy=id&direction=desc`
        );
        setLatestCoffees(coffeesResponse.data.content);
      } catch (error: any) {
        setHttpError(error.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchData();
  }, [roasteryId]);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p className="alert alert-danger">{httpError}</p>
      </div>
    );
  }

  // If roastery details are not found (e.g. 404)
  if (!roastery) {
    return (
      <div className="container mt-5">
        <p className="lead">Roastery not found.</p>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <div className="row">
        {/* Column for roastery image */}
        <div className="col-12 col-md-4 col-lg-3 mb-3" style={{ maxWidth: "400px" }}>
          {roastery.imageUrl ? (
            <img
              src={`${import.meta.env.VITE_IMAGE_BASE_URL}${roastery.imageUrl}`}
              alt="Roastery"
              className="img-fluid"
              style={{ maxHeight: "400px", objectFit: "contain" }}
            />
          ) : (
            <div>No Image Available</div>
          )}
        </div>

        {/* Column for roastery details */}
        <div className="col-12 col-md-8 col-lg-5 mb-3">
          <h2>{roastery.name}</h2>
          <p>
            <strong>Country:</strong> {roastery.country}
          </p>
          <p>
            <strong>Founded in:</strong> {roastery.foundingYear}
          </p>
          {roastery.websiteUrl && (
            <p>
              <strong>Website:</strong>{" "}
              <a href={roastery.websiteUrl} target="_blank" rel="noopener noreferrer">
                {roastery.websiteUrl}
              </a>
            </p>
          )}
        </div>

        {/* Column for featured coffee component */}
        <div className="col-12 col-lg-4">
          <FeaturedCoffee roasteryId={roasteryId} />
        </div>
      </div>

      <hr />

      {/* Section displaying the latest added coffees */}
      <LatestCoffees coffees={latestCoffees} roasteryName={roastery.name} />
    </div>
  );
};