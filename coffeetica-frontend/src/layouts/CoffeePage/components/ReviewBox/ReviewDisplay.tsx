import React, { useState } from "react";
import { ReviewDTO } from "../../../../models/ReviewDTO";
import { StarsDisplay } from "../../../Utils/reviews/StarsDisplay";

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
  const [showModal, setShowModal] = useState(false);
  const [isExpanded, setIsExpanded] = useState(false);

  const MAX_LENGTH = 100;
  const contentToShow =
    review.content.length > MAX_LENGTH && !isExpanded
      ? review.content.substring(0, MAX_LENGTH) + "..."
      : review.content;


  const toggleReadMore = () => setIsExpanded(!isExpanded);

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
        <p className="card-text">
          <strong>Brewing Method:</strong> {review.brewingMethod}
        </p>
      </div>

      {/* Brewing Description */}
      {review.brewingDescription && (
        <div className="mb-3">
          <p className="card-text">
            <strong>Brewing Description:</strong> {review.brewingDescription}
          </p>
        </div>
      )}

      {/* Review */}
      <div className="mb-3">
        <p className="card-text">
          <strong>Description:</strong> {contentToShow}
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

      <hr />
      <div>
        <button className="btn btn-secondary me-2" onClick={onEdit}>
          Edit
        </button>
        <button className="btn btn-danger" onClick={() => setShowModal(true)}>
          Delete
        </button>
      </div>

      {/* Bootstrap Modal for Delete Confirmation */}
      {showModal && (
        <div className="modal fade show d-block" tabIndex={-1} role="dialog">
          <div className="modal-dialog" role="document">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">Confirm Deletion</h5>
                <button type="button" className="btn-close" onClick={() => setShowModal(false)}></button>
              </div>
              <div className="modal-body">
                <p>Are you sure you want to delete this review? This action cannot be undone.</p>
              </div>
              <div className="modal-footer">
                <button className="btn btn-secondary" onClick={() => setShowModal(false)}>
                  Cancel
                </button>
                <button className="btn btn-danger" onClick={() => { onDelete(); setShowModal(false); }}>
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Background overlay for modal */}
      {showModal && <div className="modal-backdrop fade show"></div>}
    </div>
  );
};