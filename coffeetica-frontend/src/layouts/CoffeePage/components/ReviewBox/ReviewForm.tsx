
import React, { useState } from "react";
import { ReviewRequestDTO } from "../../../../models/ReviewRequestDTO";
import { StarsSelector } from "../../../Utils/reviews/StarsSelector";
import { ReviewDTO } from "../../../../models/ReviewDTO";

interface ReviewFormProps {
  coffeeId: number;
  initialReview?: ReviewDTO; // If exists, it's edit mode
  onSubmit: (data: ReviewRequestDTO) => void; 
  onCancel?: () => void; // "Cancel" button for edit mode
}

export const ReviewForm: React.FC<ReviewFormProps> = ({
  coffeeId,
  initialReview,
  onSubmit,
  onCancel,
}) => {
  // Form state:
  const [starInput, setStarInput] = useState<number>(
    initialReview ? initialReview.rating : 0
  );
  const [reviewDescription, setReviewDescription] = useState<string>(
    initialReview ? initialReview.content : ""
  );
  const [brewingMethod, setBrewingMethod] = useState<string>(
    initialReview ? initialReview.brewingMethod : ""
  );
  const [brewingDescription, setBrewingDescription] = useState<string>(
    initialReview ? initialReview.brewingDescription || "" : ""
  );

  const handleSubmit = () => {
    // Create a ReviewRequestDTO object (used for both POST/PUT)
    const reviewData: ReviewRequestDTO = {
      coffeeId,
      rating: starInput,
      content: reviewDescription,
      brewingMethod,
      brewingDescription,
    };
    onSubmit(reviewData);
  };

  return (
    <div style={{ cursor: "pointer" }}>
      <div className="mt-2">
        <StarsSelector
          rating={starInput}
          onRatingChange={(value) => setStarInput(value)}
        />
      </div>

      <form method="POST" action="#" onSubmit={(e) => e.preventDefault()}>
        <hr />
        {/* Brewing Method */}
        <div className="mb-3">
          <label className="form-label">Brewing Method <small className="text-muted">(max: 50)</small></label>
          <input
            type="text"
            className="form-control"
            placeholder="e.g. French Press, Espresso"
            value={brewingMethod}
            onChange={(e) => setBrewingMethod(e.target.value)}
            maxLength={50}
            required
          />
        </div>

        {/* Brewing Description */}
        <div className="mb-3">
          <label className="form-label">Brewing Description <small className="text-muted">(max: 200)</small></label>
          <textarea
            className="form-control"
            placeholder="Describe your brewing process..."
            rows={2}
            value={brewingDescription}
            onChange={(e) => setBrewingDescription(e.target.value)}
            maxLength={200}
            required
          />
        </div>

        {/* Review Description */}
        <div className="mb-3">
          <label className="form-label">Review Description</label>
          <textarea
            className="form-control"
            placeholder="Write your review here..."
            rows={3}
            value={reviewDescription}
            onChange={(e) => setReviewDescription(e.target.value)}
            required
          />
        </div>

        <button
          type="button"
          onClick={handleSubmit}
          className="btn btn-primary mt-3 me-2"
        >
          Submit Review
        </button>

        {onCancel && (
          <button
            type="button"
            onClick={onCancel}
            className="btn btn-secondary mt-3"
          >
            Cancel
          </button>
        )}
      </form>
    </div>
  );
};