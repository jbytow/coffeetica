import React from "react";
import { Link } from "react-router-dom";
import { ReviewDTO } from "../../../models/ReviewDTO";
import { Review } from "../../Utils/Review";
import { StarsReview } from "../../Utils/StarsReview";

export const LatestReviews: React.FC<{
    reviews: ReviewDTO[];
    coffeeId: number | undefined;
    mobile: boolean;
}> = (props) => {
    // Obliczanie średniej oceny
    const averageRating = calculateAverageRating(props.reviews);

    return (
        <div className={props.mobile ? "mt-3" : "row mt-5"}>
            {/* Średnia ocena nad sekcją recenzji */}
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h2>Latest Reviews</h2>
                {props.reviews.length > 0 && (
                    <div className="d-flex align-items-center">
                        <StarsReview rating={averageRating} size={32} />
                        <span className="ms-2">({props.reviews.length} reviews)</span>
                    </div>
                )}
            </div>

            <div className="col-sm-10 col-md-10">
                {props.reviews.length > 0 ? (
                    <>
                        {props.reviews.slice(0, 3).map((eachReview) => (
                            <Review review={eachReview} key={eachReview.id} />
                        ))}

                        <div className="m-3">
                            <Link
                                type="button"
                                className="btn main-color btn-md text-white"
                                to={`/reviewlist/${props.coffeeId}`}
                            >
                                View All Reviews
                            </Link>
                        </div>
                    </>
                ) : (
                    <div className="m-3">
                        <p className="lead">
                            Currently, there are no reviews for this coffee.
                        </p>
                    </div>
                )}
            </div>
        </div>
    );
};

// 🔹 Funkcja obliczająca średnią ocenę dla recenzji
function calculateAverageRating(reviews: ReviewDTO[]): number {
    if (!reviews.length) return 0;
    const total = reviews.reduce((sum, review) => sum + review.rating, 0);
    return Math.round((total / reviews.length) * 2) / 2; // Zaokrąglanie do najbliższej 0.5
}