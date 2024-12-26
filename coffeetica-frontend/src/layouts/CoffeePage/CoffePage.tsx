import { useEffect, useState } from "react";
import { CoffeeDTO } from "../../models/CoffeeDTO";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { StarsReview } from "../Utils/StarsReview";


export const CoffeePage = () => {
    // State for coffee details
    const [coffee, setCoffee] = useState<CoffeeDTO | undefined>();
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);
  
    // Extracting coffeeId from the current URL
    const coffeeId = window.location.pathname.split("/")[2];
  
    // Fetch coffee details
    useEffect(() => {
      const fetchCoffee = async () => {
        try {
          const response = await apiClient.get<CoffeeDTO>(`/coffees/${coffeeId}`);
          setCoffee(response.data);
        } catch (error: any) {
          setHttpError(error.message);
        } finally {
          setIsLoading(false);
        }
      };
      fetchCoffee();
    }, [coffeeId]);
  
    // Handle loading state
    if (isLoading) {
      return <SpinnerLoading />;
    }
  
    // Handle HTTP errors
    if (httpError) {
      return (
        <div className="container m-5">
          <p>{httpError}</p>
        </div>
      );
    }
  
    return (
      <div>
        {/* Desktop version */}
        <div className="container d-none d-lg-block">
          <div className="row mt-5">
            <div className="col-sm-2 col-md-2">
              {coffee?.imageUrl ? (
                <img
                    src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
                  width="226"
                  height="349"
                  alt="Coffee"
                />
              ) : (
                <div>No Image Available</div>
              )}
            </div>
            <div className="col-4 col-md-4 container">
              <div className="ml-2">
                <h2>{coffee?.name}</h2>
                <p>
                  <strong>Country of Origin:</strong> {coffee?.countryOfOrigin}
                </p>
                <p>
                  <strong>Region:</strong> {coffee?.region}
                </p>
                <p>
                  <strong>Roast Level:</strong> {coffee?.roastLevel}
                </p>
                <p>
                  <strong>Flavor Profile:</strong> {coffee?.flavorProfile}
                </p>
                <p>
                  <strong>Notes:</strong> {coffee?.notes}
                </p>
                <p>
                  <strong>Processing Method:</strong> {coffee?.processingMethod}
                </p>
                <p>
                  <strong>Production Year:</strong> {coffee?.productionYear}
                </p>
                <StarsReview rating={0} size={32} />
                {/* Placeholder for reviews */}
                <div>
                  <p>Reviews placeholder...</p>
                </div>
              </div>
            </div>
          </div>
        </div>
  
        {/* Mobile version */}
        <div className="container d-lg-none mt-5">
          <div className="d-flex justify-content-center align-items-center">
            {coffee?.imageUrl ? (
              <img
                src={`${import.meta.env.VITE_API_BASE_URL}${coffee.imageUrl}`}
                width="226"
                height="349"
                alt="Coffee"
              />
            ) : (
              <div>No Image Available</div>
            )}
          </div>
          <div className="mt-4">
            <div className="ml-2">
              <h2>{coffee?.name}</h2>
              <p>
                <strong>Country of Origin:</strong> {coffee?.countryOfOrigin}
              </p>
              <p>
                <strong>Region:</strong> {coffee?.region}
              </p>
              <p>
                <strong>Roast Level:</strong> {coffee?.roastLevel}
              </p>
              <p>
                <strong>Flavor Profile:</strong> {coffee?.flavorProfile}
              </p>
              <p>
                <strong>Notes:</strong> {coffee?.notes}
              </p>
              <p>
                  <strong>Processing Method:</strong> {coffee?.processingMethod}
              </p>
              <p>
                  <strong>Production Year:</strong> {coffee?.productionYear}
              </p>
              <StarsReview rating={0} size={32} />
              {/* Placeholder for reviews */}
              <div>
                <p>Reviews placeholder...</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  };