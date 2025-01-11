import { useEffect, useState } from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
import { Link } from "react-router-dom";
import apiClient from "../../../lib/api";

export const CoffeeCarousel: React.FC = () => {
  const [coffees, setCoffees] = useState<CoffeeDTO[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCoffees = async () => {
      try {
        const response = await apiClient.get<CoffeeDTO[]>("/coffees", {
          params: { page: 0, size: 9 },
        });

        const sortedCoffees = response.data.sort((a, b) => b.id - a.id); // Sort by ID descending
        setCoffees(sortedCoffees);
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

  return (
    <div className="container mt-5">
      <div className="homepage-carousel-title">
        <h3>Discover Your Next Favorite Coffee</h3>
      </div>

      <div
        id="carouselExampleControls"
        className="carousel carousel-dark slide mt-5 d-none d-lg-block"
        data-bs-ride="carousel"
      >
        <div className="carousel-inner px-5">
          {[0, 3, 6].map((startIdx, index) => (
            <div
              className={`carousel-item ${index === 0 ? "active" : ""}`}
              key={startIdx}
            >
              <div className="row d-flex justify-content-center align-items-center">
                {coffees.slice(startIdx, startIdx + 3).map((coffee) => (
                  <div className="col-auto" key={coffee.id}>
                    <Link to={`/coffees/${coffee.id}`} className="card-link">
                      <div className="card text-center m-3 custom-card-size">
                        {coffee.imageUrl ? (
                          <img
                            src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
                            alt={coffee.name}
                            className="card-img-top img-150"
                          />
                        ) : (
                          <div className="card-img-top no-image-placeholder">
                            <p>No Image Available</p>
                          </div>
                        )}
                        <div className="card-body">
                          <h5 className="card-title">{coffee.name}</h5>
                          {coffee.roastery && (
                            <p className="card-text">
                              Roastery: {coffee.roastery.name}
                            </p>
                          )}
                          <p className="card-text">
                            Country: {coffee.countryOfOrigin}
                          </p>
                        </div>
                      </div>
                    </Link>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        <button
          className="carousel-control-prev"
          type="button"
          data-bs-target="#carouselExampleControls"
          data-bs-slide="prev"
        >
          <span className="carousel-control-prev-icon" aria-hidden="true"></span>
          <span className="visually-hidden">Previous</span>
        </button>
        <button
          className="carousel-control-next"
          type="button"
          data-bs-target="#carouselExampleControls"
          data-bs-slide="next"
        >
          <span className="carousel-control-next-icon" aria-hidden="true"></span>
          <span className="visually-hidden">Next</span>
        </button>
      </div>

      <div className="homepage-carousel-title mt-3 text-center">
        <Link className="btn btn-outline-secondary btn-lg" to="/coffees">
          View All Coffees
        </Link>
      </div>
    </div>
  );
};