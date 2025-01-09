import React from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { LeaveAReview } from "../../Utils/LeaveAReview";
import { StarsReview } from "../../Utils/StarsReview";
import { ReviewDTO } from "../../../models/ReviewDTO";

export const ReviewBox: React.FC<{
    coffee: CoffeeDTO | undefined;
    mobile: boolean;
    isAuthenticated: boolean;
    isReviewLeft: boolean;
    submitReview: (rating: number, content: string) => void;
}> = (props) => {
    function reviewRender() {
        if (props.isAuthenticated && !props.isReviewLeft) {
            return (
                <p>
                    <LeaveAReview submitReview={props.submitReview} />
                </p>
            );
        } else if (props.isAuthenticated && props.isReviewLeft) {
            return (
                <p>
                    <b>Thank you for your review!</b>
                </p>
            );
        }
        return (
            <div>
                <hr />
                <p>Sign in to be able to leave a review.</p>
            </div>
        );
    }

    return (
        <div className={props.mobile ? 'card d-flex mt-5' : 'card col-3 container d-flex mb-5'}>
            <div className="card-body container">
                <div className="mt-3">
                    <h4 className="text-success">Reviews</h4>
                    <div>
                        <p className="lead">
                            <b>{props.coffee?.reviews?.length || 0}</b> total reviews
                        </p>
                        <StarsReview
                            rating={
                                props.coffee?.reviews
                                    ? calculateAverageRating(props.coffee.reviews)
                                    : 0
                            }
                            size={32}
                        />
                    </div>
                    <hr />
                    {reviewRender()}
                </div>
            </div>
        </div>
    );
};

function calculateAverageRating(reviews: ReviewDTO[]): number {
    if (!reviews.length) return 0;
    const total = reviews.reduce((sum, review) => sum + review.rating, 0);
    return Math.round((total / reviews.length) * 2) / 2; // Round to nearest 0.5
}