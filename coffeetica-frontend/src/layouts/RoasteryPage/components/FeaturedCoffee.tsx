import { useEffect, useState } from "react";
import apiClient from "../../../lib/api";
import { CoffeeDetailsDTO } from "../../../models/CoffeeDetailsDTO";


interface FeaturedCoffeeProps {
    roasteryId: number | null;
  }
  
  export const FeaturedCoffee: React.FC<FeaturedCoffeeProps> = ({ roasteryId }) => {
    const [featuredCoffee, setFeaturedCoffee] = useState<CoffeeDetailsDTO | null>(null);
  
    useEffect(() => {
      const fetchFeaturedCoffee = async () => {
        if (!roasteryId) return;
        try {
          // Pobieramy wszystkie kawy z palarni i sortujemy po najwyższej ocenie
          const response = await apiClient.get<CoffeeDetailsDTO[]>(`/coffees?roasteryId=${roasteryId}&size=5&sortBy=id&direction=desc`);
          
          // Znalezienie kawy z najwyższą oceną
          const bestCoffee = response.data.reduce((prev, current) => {
            return (prev.averageRating > current.averageRating) ? prev : current;
          }, response.data[0]);
  
          if (bestCoffee) {
            setFeaturedCoffee(bestCoffee);
          }
        } catch (error) {
          console.error("Error fetching featured coffee:", error);
        }
      };
      fetchFeaturedCoffee();
    }, [roasteryId]);
  
    return (
      <section className="featured-coffee">
        <h4>Best Rated Coffee</h4>
        <hr />
        {featuredCoffee ? (
          <div className="card">
            {featuredCoffee.imageUrl && (
              <img
                src={`${import.meta.env.VITE_API_BASE_URL}${featuredCoffee.imageUrl}`}
                className="card-img-top"
                alt={featuredCoffee.name}
                style={{ height: "200px", objectFit: "cover" }}
              />
            )}
            <div className="card-body">
              <h5 className="card-title">{featuredCoffee.name}</h5>
              <p className="card-text">⭐ {featuredCoffee.averageRating.toFixed(1)}</p>
              <p className="card-text"><strong>Roast Level:</strong> {featuredCoffee.roastLevel}</p>
              <a href={`/coffees/${featuredCoffee.id}`} className="btn btn-outline-primary">
                View Coffee
              </a>
            </div>
          </div>
        ) : (
          <p className="text-muted">No featured coffee available.</p>
        )}
      </section>
    );
  };