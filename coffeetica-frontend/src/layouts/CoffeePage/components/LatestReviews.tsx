import React from "react";
import { Link } from "react-router-dom";
import { ReviewDTO } from "../../../models/ReviewDTO";
import { Review } from "../../Utils/Review";

export const LatestReviews: React.FC<{
    reviews: ReviewDTO[];
    coffeeId: number | undefined;
    mobile: boolean;
}> = (props) => {

    return (
        <div className={props.mobile ? "mt-3" : "row mt-5"}>
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