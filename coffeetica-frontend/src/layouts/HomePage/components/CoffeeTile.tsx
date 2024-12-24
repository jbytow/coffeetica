import React from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";

interface CoffeeTileProps {
  coffee: CoffeeDTO;
}

export const CoffeeTile: React.FC<CoffeeTileProps> = ({ coffee }) => {
  return (
    <div className="col-4">
      <div className="card">
        <div className="card-body">
          <h5 className="card-title">{coffee.name}</h5>
          <p className="card-text">{coffee.flavorProfile}</p>
          <p className="card-text text-muted">{coffee.countryOfOrigin}</p>
        </div>
      </div>
    </div>
  );
};