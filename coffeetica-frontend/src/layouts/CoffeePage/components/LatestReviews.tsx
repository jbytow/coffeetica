import React from "react";
import { Link } from "react-router-dom";
import { ReviewDTO } from "../../../models/ReviewDTO";
import { Review } from "../../Utils/Review";

interface LatestReviewsProps {
  reviews: ReviewDTO[];
  coffeeId?: number;
  mobile: boolean;
}

export const LatestReviews: React.FC<LatestReviewsProps> = ({
  reviews,
  coffeeId,
  mobile,
}) => {
  const sectionMargin = mobile ? "mt-2 mb-3" : "mt-3 mb-4";

  return (
    <section className={`latest-reviews container ${sectionMargin}`}>
      {/* First row - Header */}
      <div className="row">
        <div className="col-12">
          <h2 className="mb-4">Latest Reviews:</h2>
        </div>
      </div>

      {/* Second row - Reviews and Button */}
      <div className="row">
        {reviews.length > 0 ? (
          <>
            {/* Display up to 3 latest reviews */}
            {reviews.slice(0, 3).map((eachReview) => (
              <div className="col-12 mb-3" key={eachReview.id}>
                <Review review={eachReview} />
              </div>
            ))}
              <hr />
            {/* Button: View All Reviews */}
            <div className="col-12">

              <Link
                type="button"
                className="btn btn-outline-primary btn-md px-4"
                to={`/reviewlist/${coffeeId}`}
              >
                View All Reviews
              </Link>
            </div>
          </>
        ) : (
          /* If no reviews are available, display a message */
          <div className="col-12">
            <p className="lead mb-0">
              Currently, there are no reviews for this coffee.
            </p>
          </div>
        )}
      </div>
    </section>
  );
};