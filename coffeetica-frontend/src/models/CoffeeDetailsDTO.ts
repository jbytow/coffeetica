import { ReviewDTO } from "./ReviewDTO";
import { RoasteryDTO } from "./RoasteryDTO";

export interface CoffeeDetailsDTO {
    id: number;
    name: string;
    countryOfOrigin: string;
    region: string;
    roastLevel: string;
    flavorProfile: string;
    flavorNotes: string[];
    processingMethod: string;
    productionYear: number;
    imageUrl: string;
    roastery: RoasteryDTO;
  
    // aggregated fields
    averageRating: number;
    totalReviewsCount: number;
    latestReviews: ReviewDTO[];
  }