import { useEffect, useState } from "react";
import apiClient from "../../../lib/api";
import { CoffeeDetailsDTO } from "../../../models/CoffeeDetailsDTO";
import { StarsDisplay } from "../../Utils/StarsDisplay";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";


interface FeaturedCoffeeProps {
    roasteryId: number | null;
  }
  
  /**
   * Component that displays the "featured" (best-rated) coffee of a given roastery.
   * Uses CoffeeDetailsDTO to display additional information like average rating.
   */
  export const FeaturedCoffee: React.FC<FeaturedCoffeeProps> = ({ roasteryId }) => {
    const [featuredCoffee, setFeaturedCoffee] = useState<CoffeeDetailsDTO | null>(null);
    const [httpError, setHttpError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
  
    // Fetch the featured coffee for the given roastery
    useEffect(() => {
      if (!roasteryId) return;
  
      const fetchFeaturedCoffee = async () => {
        setIsLoading(true);
        setHttpError(null);
  
        try {
          // Call the dedicated endpoint returning a single featured coffee
          const response = await apiClient.get<CoffeeDetailsDTO>(
            `/roasteries/${roasteryId}/featured-coffee`
          );
          setFeaturedCoffee(response.data);
        } catch (error: any) {
          // Log error and set error message for user feedback
          console.error("Error fetching featured coffee:", error);
          setHttpError("Could not load featured coffee.");
        } finally {
          setIsLoading(false);
        }
      };
  
      fetchFeaturedCoffee();
    }, [roasteryId]);
  
    if (isLoading) {
      return <SpinnerLoading />;
    }
  
    if (httpError) {
      return (
        <div className="alert alert-danger" role="alert">
          {httpError}
        </div>
      );
    }
  
    return (
      <section className="featured-coffee">
        <h4>Best Rated Coffee</h4>
        <hr />
        {featuredCoffee ? (
          <div className="card mb-4">
            <div className="row g-0">
              {/* Left column: Displays and centers the coffee image */}
              <div className="col-12 col-md-4 d-flex justify-content-center align-items-center p-2">
                {featuredCoffee.imageUrl && (
                  <div className="w-100" style={{ maxWidth: "300px" }}>
                    <img
                      src={`${import.meta.env.VITE_API_BASE_URL}${featuredCoffee.imageUrl}`}
                      alt={featuredCoffee.name}
                      className="img-fluid rounded-start object-fit-cover"
                      style={{ objectFit: "cover", aspectRatio: "3 / 4" }}
                    />
                  </div>
                )}
              </div>
  
              {/* Right column: Displays the coffee details */}
              <div className="col-md-8">
                <div className="card-body">
                  <h5 className="card-title">{featuredCoffee.name}</h5>
  
                  {/* Rating display with stars and number of reviews */}
                  <div className="d-flex align-items-center mb-2">
                    <StarsDisplay rating={featuredCoffee.averageRating ?? 0} />
                    <span className="ms-2 text-muted">
                      {featuredCoffee.totalReviewsCount ?? 0} reviews
                    </span>
                  </div>
  
                  {/* Additional coffee information */}
                  <p className="card-text mb-1">
                    <strong>Region:</strong> {featuredCoffee.region}
                  </p>
                  <p className="card-text mb-1">
                    <strong>Country:</strong> {featuredCoffee.countryOfOrigin}
                  </p>
                  <p className="card-text mb-1">
                    <strong>Flavor Profile:</strong> {featuredCoffee.flavorProfile}
                  </p>
  
                  {/* Link to coffee details */}
                  <div className="mt-3">
                    <a href={`/coffees/${featuredCoffee.id}`} className="btn btn-outline-primary">
                      View Coffee
                    </a>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <p className="text-muted">No featured coffee available.</p>
        )}
      </section>
    );
  };