import { ReviewDTO } from "./ReviewDTO";
import { RoasteryDTO } from "./RoasteryDTO";

export interface CoffeeDTO {
    id: number;
    name: string;
    countryOfOrigin: string;
    region: string;
    roastLevel: string;
    flavorProfile: string;
    notes: string;
    processingMethod: string;
    productionYear: number;
    roastery: RoasteryDTO;
    reviews: ReviewDTO[];
  }