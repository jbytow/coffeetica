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
  return (
    <section className={`latest-reviews ${mobile ? "mt-3" : "mt-5"}`}>
      <div className="row mx-0">
        {reviews.length > 0 ? (
          <>
            {reviews.slice(0, 3).map((eachReview) => (
              <div className="col-12 mb-3 p-0" key={eachReview.id}>
                <Review review={eachReview} />
              </div>
            ))}
            <div className="col-12 p-0">
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
          <div className="col-12 p-0">
            <p className="lead mb-0">
              Currently, there are no reviews for this coffee.
            </p>
          </div>
        )}
      </div>
    </section>
  );
};