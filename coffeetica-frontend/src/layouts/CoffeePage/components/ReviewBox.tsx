import React from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { LeaveAReview } from "../../Utils/LeaveAReview";

export const ReviewBox: React.FC<{
    coffee: CoffeeDTO | undefined;
    mobile: boolean;
    isAuthenticated: boolean;
    isReviewLeft: boolean;
    submitReview: (rating: number, content: string, brewingMethod: string, brewingDescription: string) => void;
}> = (props) => {
    function reviewRender() {
        if (props.isAuthenticated && !props.isReviewLeft) {
            return <LeaveAReview submitReview={props.submitReview} />;
        } else if (props.isAuthenticated && props.isReviewLeft) {
            return <p><b>Thank you for your review!</b></p>;
        }
        return (
            <div>
                <hr />
                <p>Sign in to be able to leave a review.</p>
            </div>
        );
    }

    return (
        <div className={props.mobile ? "card d-flex mt-5" : "card col-3 container d-flex mb-5"}>
            <div className="card-body container">
                <div className="mt-3">
                    <h4 className="text-success">Leave a review</h4>
                    {reviewRender()}
                </div>
            </div>
        </div>
    );
};