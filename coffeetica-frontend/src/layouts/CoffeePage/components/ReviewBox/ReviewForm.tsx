
import React, { useState } from "react";
import { ReviewRequestDTO } from "../../../../models/ReviewRequestDTO";
import { StarsSelector } from "../../../Utils/StarsSelector";
import { ReviewDTO } from "../../../../models/ReviewDTO";

interface ReviewFormProps {
  coffeeId: number;
  initialReview?: ReviewDTO; 
  onSubmit: (data: ReviewRequestDTO) => void; 
  onCancel?: () => void; // Jeśli chcesz mieć przycisk "Anuluj" w trybie edycji
}

export const ReviewForm: React.FC<ReviewFormProps> = ({
  coffeeId,
  initialReview,
  onSubmit,
  onCancel,
}) => {
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

  const handleStarChange = (value: number) => {
    setStarInput(value);
  };

  const handleSubmit = () => {
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
      {/* Komponent wyboru gwiazdek */}
      <div className="mt-2">
        <StarsSelector rating={starInput} onRatingChange={handleStarChange} />
      </div>

      <form method="POST" action="#">
        <hr />

        {/* Brewing Method */}
        <div className="mb-3">
          <label className="form-label">Brewing Method</label>
          <input
            type="text"
            className="form-control"
            placeholder="e.g. French Press, Espresso"
            value={brewingMethod}
            onChange={(e) => setBrewingMethod(e.target.value)}
            required
          />
        </div>

        {/* Brewing Description */}
        <div className="mb-3">
          <label className="form-label">Brewing Description (optional)</label>
          <textarea
            className="form-control"
            placeholder="Describe your brewing process..."
            rows={2}
            value={brewingDescription}
            onChange={(e) => setBrewingDescription(e.target.value)}
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