import React, { useState } from "react";
import { ReviewDTO } from "../../models/ReviewDTO";
import { StarsDisplay } from "./StarsDisplay";
import { Link } from "react-router-dom";

interface ReviewProps {
  review: ReviewDTO;
  /**
   * If true, show coffeeName link instead of userName text.
   */
  showCoffeeInsteadOfUser?: boolean;
}

export const Review: React.FC<ReviewProps> = ({
  review,
  showCoffeeInsteadOfUser = false,
}) => {
  // Format date
  const dateObj = new Date(review.createdAt);
  const formattedDate = dateObj.toLocaleString("en-us", {
    month: "long",
    day: "numeric",
    year: "numeric",
  });

  // Rating
  const ratingVal = parseFloat(review.rating.toString());
  const displayRating = Number.isInteger(ratingVal)
    ? `${ratingVal}/5`
    : `${ratingVal.toFixed(1)}/5`;

  // "Read more" logic
  const [isExpanded, setIsExpanded] = useState(false);
  const MAX_LENGTH = 100;
  const contentToShow =
    review.content.length > MAX_LENGTH && !isExpanded
      ? review.content.substring(0, MAX_LENGTH) + "..."
      : review.content;

  const toggleReadMore = () => setIsExpanded(!isExpanded);

  // Decide the heading
  const headingElement = showCoffeeInsteadOfUser ? (
    <h5 className="mb-0 me-3 align-self-center">
      <Link to={`/coffees/${review.coffeeId}`}>{review.coffeeName}</Link>
    </h5>
  ) : (
    <h5 className="mb-0 me-3 align-self-center">{review.userName}</h5>
  );

  return (
    <article className="card review">
      <div className="card-header d-flex align-items-center">
        {headingElement}
        <div className="d-flex align-items-center ms-2">
          <StarsDisplay rating={review.rating} size={20} />
          <span className="ms-2 text-muted">{displayRating}</span>
        </div>
        <small className="text-muted ms-auto">{formattedDate}</small>
      </div>

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