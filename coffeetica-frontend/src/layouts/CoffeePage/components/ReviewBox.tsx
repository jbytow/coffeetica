import React, { useContext } from "react";
import { CoffeeDTO } from "../../../models/CoffeeDTO";
import { LeaveAReview } from "../../Utils/LeaveAReview";
import { ReviewRequestDTO } from "../../../models/ReviewRequestDTO";
import { ReviewDTO } from "../../../models/ReviewDTO";
import { AuthContext } from "../../../auth/AuthContext";
import { Link } from "react-router-dom";

export const ReviewBox: React.FC<{
    coffee: CoffeeDTO | undefined;
    userReview: ReviewDTO | null;
    submitReview: (reviewData: ReviewRequestDTO) => void;
}> = ({ coffee, userReview, submitReview }) => {
    const { isAuthenticated } = useContext(AuthContext);

    function reviewRender() {
        if (!coffee?.id) {
            return null;
        }

        if (!isAuthenticated) {
            return (
                <div>
                    <hr />
                    <p>Sign in to be able to leave a review.</p>
                    <Link to="/login" className="btn btn-primary">Sign in</Link>
                </div>
            );
        }

        if (userReview) {
            return (
                <div>
                    <p><b>Your review:</b> {userReview.content}</p>
                    <p><b>Rating:</b> {userReview.rating}</p>
                    <p><b>Brewing Method:</b> {userReview.brewingMethod}</p>
                    <hr />
                    <p><b>Thank you for your review!</b></p>
                </div>
            );
        }

        return <LeaveAReview submitReview={submitReview} coffeeId={coffee.id} />;
    }

    return (
        <div className="card col-3 container d-flex mb-5">
            <div className="card-body container">
                <h4 className="text-success">Leave a review</h4>
                {reviewRender()}
            </div>
        </div>
    );
};