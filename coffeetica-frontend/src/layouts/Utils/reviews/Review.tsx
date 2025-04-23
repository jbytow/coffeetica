import React, { useContext, useState } from "react";
import { ReviewDTO } from "../../../models/ReviewDTO";
import { StarsDisplay } from "./StarsDisplay";
import { Link } from "react-router-dom";
import { AuthContext } from "../../../auth/AuthContext";
import apiClient from "../../../lib/api";

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
  const { hasRole } = useContext(AuthContext); // Pobieramy rolę użytkownika
  const isAdmin = hasRole("Admin"); // Sprawdzamy, czy użytkownik jest Adminem

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

  const handleDeleteReview = async () => {
    if (window.confirm("Are you sure you want to delete this review?")) {
      try {
        await apiClient.delete(`/reviews/${review.id}`);
        alert("Review deleted successfully!");
        window.location.reload();
      } catch (error: any) {
        alert("Error deleting review: " + error.message);
      }
    }
  };

  // Decide the heading
  const headingElement = showCoffeeInsteadOfUser ? (
    <h5 className="mb-0 me-3 align-self-center">
      <Link to={`/coffees/${review.coffeeId}`}>{review.coffeeName}</Link>
    </h5>
  ) : (
    <h5 className="mb-0 me-3 align-self-center">
      <Link to={`/users/${review.userId}`}>{review.userName}</Link>
    </h5>
  );

  return (
    <article className="card review">
      {/* Desktop */}
      <div className="card-header d-none d-md-block">
        <div className="d-flex align-items-center justify-content-between">
          <div className="d-flex align-items-center flex-grow-1">
            <div className="me-3" style={{ minWidth: 0 }}>
              {headingElement}
            </div>
            <div className="d-flex align-items-center flex-shrink-0">
              <StarsDisplay rating={review.rating} size={20} />
              <span className="ms-2 text-muted">{displayRating}</span>
            </div>
          </div>
          <small className="text-muted ms-3">{formattedDate}</small>
        </div>
      </div>

      {/* Mobile */}
      <div className="card-header d-md-none">
        <div className="d-flex flex-column">
          <div className="text-truncate mb-2" style={{ minWidth: 0 }}>
            {headingElement}
          </div>
          <div className="d-flex justify-content-between align-items-center">
            <div className="d-flex align-items-center">
              <StarsDisplay rating={review.rating} size={20} />
              <span className="ms-2 text-muted">{displayRating}</span>
            </div>
            <small className="text-muted">{formattedDate}</small>
          </div>
        </div>
      </div>

      <div className="card-body text-break">
        <p className="mb-2">
          <strong>Brewing Method:</strong> {review.brewingMethod}
        </p>
        <p className="mb-2">
          <strong>Brewing Description:</strong> {review.brewingDescription}
        </p>
        <p className="mb-2 text-break">
          {contentToShow}
          {review.content.length > MAX_LENGTH && (
            <button
              onClick={toggleReadMore}
              className="btn btn-link p-0 ms-1 text-nowrap align-baseline text-decoration-underline"
              style={{ fontSize: "0.9rem" }}
            >
              {isExpanded ? "Show less" : "Read more"}
            </button>
          )}
        </p>
      </div>

      {/* Delete button for admin only */}
      {isAdmin && (
        <div className="card-footer text-end">
          <button className="btn btn-danger btn-sm" onClick={handleDeleteReview}>
            Delete
          </button>
        </div>
      )}
    </article>
  );
};