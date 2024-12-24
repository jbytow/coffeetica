import { useEffect, useState } from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { CoffeeTile } from "./CoffeeTile";
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
        const response = await apiClient.get("/coffees", {
          params: { page: 0, size: 9 },
        });
        const loadedCoffees: CoffeeDTO[] = response.data.content || [];
        setCoffees(loadedCoffees);
        setIsLoading(false);
      } catch (error: any) {
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
    <div className="container mt-5" style={{ height: 550 }}>
      <div className="homepage-carousel-title">
        <h3>Discover Your Next Favorite Coffee</h3>
      </div>
      <div
        id="carouselExampleControls"
        className="carousel carousel-dark slide mt-5 d-none d-lg-block"
        data-bs-interval="false"
      >
        {/* Desktop */}
        <div className="carousel-inner">
          <div className="carousel-item active">
            <div className="row d-flex justify-content-center align-items-center">
              {coffees.slice(0, 3).map((coffee) => (
                <CoffeeTile coffee={coffee} key={coffee.id} />
              ))}
            </div>
          </div>
          <div className="carousel-item">
            <div className="row d-flex justify-content-center align-items-center">
              {coffees.slice(3, 6).map((coffee) => (
                <CoffeeTile coffee={coffee} key={coffee.id} />
              ))}
            </div>
          </div>
          <div className="carousel-item">
            <div className="row d-flex justify-content-center align-items-center">
              {coffees.slice(6, 9).map((coffee) => (
                <CoffeeTile coffee={coffee} key={coffee.id} />
              ))}
            </div>
          </div>
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
      {/* Mobile */}
      <div className="d-lg-none mt-3">
        <div className="row d-flex justify-content-center align-items-center">
          {coffees.length > 0 && <CoffeeTile coffee={coffees[0]} key={coffees[0].id} />}
        </div>
      </div>
      <div className="homepage-carousel-title mt-3">
        <Link className="btn btn-outline-secondary btn-lg" to="/coffees">
          View All Coffees
        </Link>
      </div>
    </div>
  );
};