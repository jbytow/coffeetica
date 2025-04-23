import React from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { Link } from "react-router-dom";

interface LatestCoffeesProps {
    coffees: CoffeeDTO[];
    roasteryName?: string;  // Teraz roasteryName jest opcjonalne
  }
  
  export const LatestCoffees: React.FC<LatestCoffeesProps> = ({ coffees, roasteryName }) => {
    return (
      <section className="latest-coffees container mt-3 mb-4">
        {/* Header */}
        <div className="row">
          <div className="col-12">
            <h2 className="mb-4">Latest Added Coffees</h2>
          </div>
        </div>
  
        {/* Coffee List */}
        {coffees.length > 0 ? (
          <>
            {coffees.map((coffee) => (
              <div className="card mt-3 shadow p-3 mb-3 bg-body rounded" key={coffee.id}>
                <div className="row g-0">
                  {/* Coffee Image */}
                  <div className="col-md-3 d-flex justify-content-center align-items-center">
                    <div>
                      {coffee.imageUrl ? (
                        <img
                          src={`${import.meta.env.VITE_IMAGE_BASE_URL}${coffee.imageUrl}`}
                          width="123"
                          height="196"
                          alt={coffee.name}
                          style={{ objectFit: "cover", borderRadius: "5px" }}
                        />
                      ) : (
                        <img
                          src="https://via.placeholder.com/123x196"
                          width="123"
                          height="196"
                          alt="Placeholder Coffee"
                          style={{ objectFit: "cover", borderRadius: "5px" }}
                        />
                      )}
                    </div>
                  </div>
  
                  {/* Coffee Info */}
                  <div className="col-md-9">
                    <div className="card-body">
                      <div className="row align-items-center mb-2">
                        <div className="col-md-5">
                          <Link to={`/coffees/${coffee.id}`} className="fs-4 text-decoration-none">
                            <h5 className="card-title">{coffee.name}</h5>
                          </Link>
                        </div>
                      </div>
  
                      <div className="row">
                        <div className="col-md-5 mb-3 mb-md-0">
                          <p className="card-text"><strong>Roastery:</strong> {coffee.roastery.name}</p>
                          <p className="card-text"><strong>Region:</strong> {coffee.region}</p>
                          <p className="card-text"><strong>Country:</strong> {coffee.countryOfOrigin}</p>
                          <p className="card-text"><strong>Year:</strong> {coffee.productionYear}</p>
                        </div>
                        <div className="col-md-5">
                          <p className="card-text"><strong>Roast Level:</strong> {coffee.roastLevel}</p>
                          <p className="card-text"><strong>Flavor Profile:</strong> {coffee.flavorProfile}</p>
                          <p className="card-text"><strong>Flavor Notes:</strong> {coffee.flavorNotes.join(", ")}</p>
                          <p className="card-text"><strong>Processing:</strong> {coffee.processingMethod}</p>
                        </div>
                      </div>
                      <Link to={`/coffees/${coffee.id}`} className="btn btn-primary mt-3">
                        View Coffee
                      </Link>
                    </div>
                  </div>
                </div>
              </div>
            ))}
  
            <hr />
  
            {/* "View All Coffees" Button */}
            {roasteryName && (
              <div className="row">
                <div className="col-12">
                  <Link
                    type="button"
                    className="btn btn-outline-primary btn-md px-4"
                    to={`/coffees?roasteryName=${encodeURIComponent(roasteryName)}`}
                  >
                    View All Coffees
                  </Link>
                </div>
              </div>
            )}
          </>
        ) : (
          <div className="col-12">
            <p className="lead">No coffees available yet.</p>
          </div>
        )}
      </section>
    );
  };