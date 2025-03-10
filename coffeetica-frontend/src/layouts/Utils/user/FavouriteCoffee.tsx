import { useEffect, useState } from "react";
import { CoffeeDetailsDTO } from "../../../models/CoffeeDetailsDTO";
import apiClient from "../../../lib/api";
import { SpinnerLoading } from "../ui/SpinnerLoading";
import { StarsDisplay } from "../reviews/StarsDisplay";

interface FavouriteCoffeeProps {
  userId: number;
}

/**
 * Fetches and displays the user's favorite coffee (last coffee with rating == 5),
 * without surrounding card layout. The parent component should handle
 * the card or container styling.
 */
export const FavouriteCoffee: React.FC<FavouriteCoffeeProps> = ({ userId }) => {
  const [coffee, setCoffee] = useState<CoffeeDetailsDTO | null>(null);
  const [httpError, setHttpError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const fetchFavouriteCoffee = async () => {
      setIsLoading(true);
      setHttpError(null);

      try {
        const response = await apiClient.get<CoffeeDetailsDTO>(
          `/users/${userId}/favorite-coffee`
        );
        setCoffee(response.data);
      } catch (error: any) {
        console.error("Error fetching favorite coffee:", error);
        setHttpError("Could not load favorite coffee.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchFavouriteCoffee();
  }, [userId]);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return <div className="alert alert-danger">{httpError}</div>;
  }

  if (!coffee) {
    return <p className="text-muted">No favorite coffee found.</p>;
  }

  return (
    <div className="row g-0">
      {/* Coffee image section */}
      <div className="col-12 col-md-4 d-flex justify-content-center align-items-center p-2">
        {coffee.imageUrl && (
          <div style={{ maxWidth: "300px", width: "100%" }}>
            <img
              src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
              alt={coffee.name}
              className="img-fluid rounded object-fit-cover"
              style={{ objectFit: "cover", aspectRatio: "3 / 4" }}
            />
          </div>
        )}
      </div>
      {/* Coffee details */}
      <div className="col-md-8">
        <h5>{coffee.name}</h5>
        <div className="d-flex align-items-center mb-2">
          <StarsDisplay rating={coffee.averageRating ?? 0} />
          <span className="ms-2 text-muted">
            {coffee.totalReviewsCount ?? 0} reviews
          </span>
        </div>
        <p className="mb-1">
          <strong>Region:</strong> {coffee.region}
        </p>
        <p className="mb-1">
          <strong>Country:</strong> {coffee.countryOfOrigin}
        </p>
        <p className="mb-1">
          <strong>Flavor Profile:</strong> {coffee.flavorProfile}
        </p>
        <div className="mt-3">
          <a
            href={`/coffees/${coffee.id}`}
            className="btn btn-outline-primary"
          >
            View Coffee
          </a>
        </div>
      </div>
    </div>
  );
};