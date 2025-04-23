import { useEffect, useState } from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { SpinnerLoading } from "../../Utils/ui/SpinnerLoading";
import { Link } from "react-router-dom";
import apiClient from "../../../lib/api";

export const CoffeeCarousel: React.FC = () => {
  const [coffees, setCoffees] = useState<CoffeeDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCoffees = async () => {
      try {
        const response = await apiClient.get("/coffees", {
          params: { page: 0, size: 9 },
        });
  
        setCoffees(response.data.content);
        setIsLoading(false);
      } catch (error: any) {
        console.error("Error fetching coffees:", error);
        setHttpError(error.response?.data?.message || "Something went wrong!");
        setIsLoading(false);
      }
    };
  
    fetchCoffees();
  }, []);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  if (coffees.length === 0) {
    return (
      <div className="container mt-5 text-center">
        <p className="lead">No coffees available to display at the moment.</p>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <div className="homepage-carousel-title">
        <h3>Discover Your Next Favorite Coffee</h3>
      </div>

      {/* Desktop */}
      <div
        id="carouselExampleControls"
        className="carousel carousel-dark slide mt-5 d-none d-lg-block"
        data-bs-ride="carousel"
      >
        <div className="carousel-inner px-5">
          {[0, 3, 6].map((startIdx, index) => {
            const group = coffees.slice(startIdx, startIdx + 3);
            if (group.length === 0) return null; // Skip empty groups

            return (
              <div
                className={`carousel-item ${index === 0 ? "active" : ""}`}
                key={startIdx}
              >
                <div className="row d-flex justify-content-center">
                  {group.map((coffee) => (
                    <div className="col-auto" key={coffee.id}>
                      <Link to={`/coffees/${coffee.id}`} className="card-link">
                        <div className="coffee-carousel-card m-3 shadow-sm">
                          <img
                            src={`${import.meta.env.VITE_IMAGE_BASE_URL}${coffee.imageUrl}`}
                            alt={coffee.name}
                            className="coffee-carousel-image"
                          />
                          <div className="card-body d-flex flex-column p-3">
                            <h5>{coffee.roastery?.name} {coffee.name}</h5>
                            <p className="text-muted mt-auto mb-0">
                              {coffee.countryOfOrigin} - {coffee.flavorProfile}
                            </p>
                          </div>
                        </div>
                      </Link>
                    </div>
                  ))}
                </div>
              </div>
            );
          })}
        </div>

        {coffees.length > 3 && (
          <>
            <button
              className="carousel-control-prev"
              type="button"
              data-bs-target="#carouselExampleControls"
              data-bs-slide="prev"
            >
              <span
                className="carousel-control-prev-icon"
                aria-hidden="true"
              ></span>
              <span className="visually-hidden">Previous</span>
            </button>
            <button
              className="carousel-control-next"
              type="button"
              data-bs-target="#carouselExampleControls"
              data-bs-slide="next"
            >
              <span
                className="carousel-control-next-icon"
                aria-hidden="true"
              ></span>
              <span className="visually-hidden">Next</span>
            </button>
          </>
        )}
      </div>

      {/* Mobile */}
      <div className="d-lg-none mt-3">
        <div className="row d-flex justify-content-center align-items-center">
          {coffees.slice(0, 1).map((coffee) => (
            <div className="col-auto" key={coffee.id}>
              <Link to={`/coffees/${coffee.id}`} className="card-link">
                <div className="coffee-carousel-card m-3 shadow-sm">
                  <img
                    src={`${import.meta.env.VITE_IMAGE_BASE_URL}${coffee.imageUrl}`}
                    alt={coffee.name}
                    className="coffee-carousel-image"
                  />
                  <div className="card-body d-flex flex-column p-3">
                    <h5>{coffee.roastery?.name} {coffee.name}</h5>
                    <p className="text-muted mt-auto mb-0">
                      {coffee.countryOfOrigin} - {coffee.flavorProfile}
                    </p>
                  </div>
                </div>
              </Link>
            </div>
          ))}
        </div>
      </div>

      <div className="homepage-carousel-title mt-3 text-center">
        <Link className="btn btn-outline-secondary btn-lg" to="/coffees">
          View All Coffees
        </Link>
      </div>
    </div>
  );
};