import React from "react";
import { ReviewDTO } from "../../../../models/ReviewDTO";
import { StarsDisplay } from "../../../Utils/StarsDisplay";

interface ReviewDisplayProps {
  review: ReviewDTO;
  onEdit: () => void;
  // Możesz też dodać onDelete, jeśli chcesz obsługiwać usuwanie recenzji
}

export const ReviewDisplay: React.FC<ReviewDisplayProps> = ({ review, onEdit }) => {
  return (
    <div>
      <h5>Your Review</h5>
      
      <p>
        <strong>Rating:</strong>{" "}
        <span style={{ display: "inline-flex", alignItems: "center" }}>
          <StarsDisplay rating={review.rating} />
          <span style={{ marginLeft: "8px" }}>{review.rating}/5</span>
        </span>
      </p>
      
      <p>
        <strong>Brewing Method:</strong> {review.brewingMethod}
      </p>
      
      {review.brewingDescription && (
        <p>
          <strong>Brewing Description:</strong> {review.brewingDescription}
        </p>
      )}
      
      <p>
        <strong>Content:</strong> {review.content}
      </p>
      
      <hr />
      <button className="btn btn-secondary" onClick={onEdit}>
        Edit Review
      </button>
      {/* Jeśli chcesz, możesz dodać przycisk usuwania recenzji */}
      {/* <button className="btn btn-danger ms-2" onClick={onDelete}>Delete Review</button> */}
    </div>
  );
};