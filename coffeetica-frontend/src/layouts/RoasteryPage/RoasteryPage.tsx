import { useEffect, useState } from "react";
import apiClient from "../../lib/api";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { useParams } from "react-router-dom";
import { RoasteryDTO } from "../../models/RoasteryDTO";
import { CoffeeDTO } from "../../models/CoffeeDTO";
import { FeaturedCoffee } from "./components/FeaturedCoffee";
import { LatestCoffees } from "./components/LatestCoffees";

export const RoasteryPage = () => {
    const [roastery, setRoastery] = useState<RoasteryDTO | undefined>();
    const [latestCoffees, setLatestCoffees] = useState<CoffeeDTO[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);
  
    // Odczytujemy roasteryId z URL
    const { id } = useParams<{ id: string }>();
    const roasteryId = id ? Number(id) : null;
  
    useEffect(() => {
      const fetchData = async () => {
        if (!roasteryId) {
          setIsLoading(false);
          return;
        }
  
        try {
          // 1) Pobranie szczegółów palarni
          const roasteryResponse = await apiClient.get<RoasteryDTO>(
            `/roasteries/${roasteryId}`
          );
          setRoastery(roasteryResponse.data);
  
          // 2) Pobranie najnowszych kaw tej palarni (3 ostatnie)
          //    Zwracane jest Page<CoffeeDTO>, więc trzeba użyć .content
          const coffeesResponse = await apiClient.get<{
            content: CoffeeDTO[];
          }>(`/roasteries/${roasteryId}/coffees?page=0&size=3&sortBy=id&direction=desc`);
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
  
    // Jeśli brak danych o palarni (np. 404)
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
          {/* Kolumna z obrazkiem palarni */}
          <div
            className="col-12 col-md-4 col-lg-3 mb-3"
            style={{ maxWidth: "400px" }}
          >
            {roastery.imageUrl ? (
              <div className="ratio" style={{ aspectRatio: "300 / 400" }}>
                <img
                  src={`${import.meta.env.VITE_API_BASE_URL}${roastery.imageUrl}`}
                  alt="Roastery"
                  style={{ width: "100%", height: "100%" }}
                />
              </div>
            ) : (
              <div>No Image Available</div>
            )}
          </div>
  
          {/* Kolumna z informacjami o palarni */}
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
                <a
                  href={roastery.websiteUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                >
                  {roastery.websiteUrl}
                </a>
              </p>
            )}
          </div>
  
          {/* (Opcjonalnie) Miejsce na np. komponent FeaturedCoffee, itp. */}
          <div className="col-12 col-lg-4">
            <FeaturedCoffee roasteryId={roasteryId} />
          </div>
        </div>
  
        <hr />
  
        {/* Sekcja z najnowszymi kawami w osobnym komponencie */}
        <LatestCoffees coffees={latestCoffees} roasteryName={roastery?.name}/>
      </div>
    );
  };