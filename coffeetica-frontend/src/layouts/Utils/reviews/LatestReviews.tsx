import React from "react";
import { Link } from "react-router-dom";
import { ReviewDTO } from "../../../models/ReviewDTO";
import { Review } from "./Review";

interface LatestReviewsProps {
  reviews: ReviewDTO[];
  /**
   * If you pass coffeeId, the "View All Reviews" button goes to /coffees/:coffeeId/reviews.
   * If you pass userId, it goes to /users/:userId/reviews.
   */
  coffeeId?: number;
  userId?: number;

  /**
   * If true, each <Review> shows coffeeName instead of userName.
   * If false/undefined, it shows userName.
   */
  showCoffeeInsteadOfUser?: boolean;

  /**
   * If mobile is true, we use a smaller margin.
   */
  mobile: boolean;


  /**
   * If you want to handle asynchronous states in here:
   */
  loading?: boolean;
  error?: string | null;
}

export const LatestReviews: React.FC<LatestReviewsProps> = ({
  reviews,
  coffeeId,
  userId,
  showCoffeeInsteadOfUser = false,
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

      {/* Second row - Reviews and Button */}      <div className="row">
        {reviews.length > 0 ? (
          <>
            {reviews.map((eachReview) => (
              <div className="col-12 mb-3" key={eachReview.id}>
                <Review
                  review={eachReview}
                  showCoffeeInsteadOfUser={showCoffeeInsteadOfUser}
                />
              </div>
            ))}

            <hr />
            <div className="col-12">
              {/* If coffeeId is given, link to coffees. If userId is given, link to users. */}
              {coffeeId && (
                <Link
                  className="btn btn-outline-primary btn-md px-4"
                  to={`/coffees/${coffeeId}/reviews`}
                >
                  View All Reviews
                </Link>
              )}
              {userId && (
                <Link
                  className="btn btn-outline-primary btn-md px-4"
                  to={`/users/${userId}/reviews`}
                >
                  View All Reviews
                </Link>
              )}
            </div>
          </>
        ) : (
          <div className="col-12">
            <p className="lead mb-0">No reviews found.</p>
          </div>
        )}
      </div>
    </section>
  );
};