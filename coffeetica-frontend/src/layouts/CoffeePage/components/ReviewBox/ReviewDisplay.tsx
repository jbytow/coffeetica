import React from "react";
import { ReviewDTO } from "../../../../models/ReviewDTO";
import { StarsDisplay } from "../../../Utils/StarsDisplay";

interface ReviewDisplayProps {
  review: ReviewDTO;
  onEdit: () => void;
  onDelete: () => void;
}

export const ReviewDisplay: React.FC<ReviewDisplayProps> = ({
  review,
  onEdit,
  onDelete,
}) => {
  return (
    <div>
      <h4>Thank you for your review!</h4>
      <hr />

      {/* Rating */}
      <div className="mb-3 d-flex align-items-center">
  <StarsDisplay rating={review.rating} />
  <span className="ms-3">{review.rating}/5</span>
</div>

      {/* Brewing Method */}
      <div className="mb-3">
        <label className="form-label">
          <strong>Brewing Method:</strong> {review.brewingMethod}
        </label>
      </div>

      {/* Brewing Description */}
      {review.brewingDescription && (
        <div className="mb-3">
          <label className="form-label">
            <strong>Brewing Description:</strong> {review.brewingDescription}
          </label>
        </div>
      )}

      {/* Recenzja / Content na samym dole */}
      <div className="mb-3">
        <label className="form-label">
          <strong>Description:</strong> {review.content}
        </label>
      </div>

      <hr />
      <div>
        <button className="btn btn-secondary me-2" onClick={onEdit}>
          Edit
        </button>
        <button className="btn btn-danger" onClick={onDelete}>
          Delete
        </button>
      </div>
    </div>
  );
};