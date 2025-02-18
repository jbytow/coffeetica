import React, { useState } from "react";
import { ReviewDTO } from "../../models/ReviewDTO";
import { StarsDisplay } from "./StarsDisplay";

interface ReviewProps {
  review: ReviewDTO;
}

export const Review: React.FC<ReviewProps> = ({ review }) => {
  // Date formatting
  const dateObj = new Date(review.createdAt);
  const formattedDate = dateObj.toLocaleString("en-us", {
    month: "long",
    day: "numeric",
    year: "numeric",
  });

  // Logic for displaying the rating (e.g., 5 → 5/5, 3.5 → 3.5/5)
  const ratingValue = parseFloat(review.rating.toString());
  const displayRating = Number.isInteger(ratingValue)
    ? `${ratingValue}/5`
    : `${ratingValue.toFixed(1)}/5`;

  // "Read more" - control content length for long reviews
  const [isExpanded, setIsExpanded] = useState(false);
  const MAX_LENGTH = 100;
  const contentToShow =
    review.content.length > MAX_LENGTH && !isExpanded
      ? review.content.substring(0, MAX_LENGTH) + "..."
      : review.content;

  const toggleReadMore = () => {
    setIsExpanded((prev) => !prev);
  };

  return (
    <article className="card review">
      {/* Card header: user name, stars, and date */}
      <div className="card-header d-flex align-items-center">
  <h5 className="mb-0 me-3 align-self-center">{review.userName}</h5>
  <div className="d-flex align-items-center me-2">
    <div className="align-self-center mb-1">
      <StarsDisplay rating={review.rating} size={20} />
    </div>
    <div className="align-self-center ms-2">
      <span className="text-muted">{displayRating}</span>
    </div>
  </div>
  <small className="text-muted ms-auto align-self-center">
    {formattedDate}
  </small>
</div>

      {/* Card content */}
      <div className="card-body">
        <p className="mb-2">
          <strong>Brewing Method:</strong> {review.brewingMethod}
        </p>
        <p className="mb-2">
          <strong>Brewing Description:</strong> {review.brewingDescription}
        </p>
        <p className="mb-2">
          {contentToShow}
          {review.content.length > MAX_LENGTH && (
            <>
              {" "}
              <button
                onClick={toggleReadMore}
                className="btn btn-link p-0"
                style={{ fontSize: "0.9rem" }}
              >
                {isExpanded ? "Show less" : "Read more"}
              </button>
            </>
          )}
        </p>
      </div>
    </article>
  );
};