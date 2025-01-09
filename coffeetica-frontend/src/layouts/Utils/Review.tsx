import React from "react";
import { ReviewDTO } from "../../models/ReviewDTO";
import { StarsReview } from "./StarsReview";


export const Review: React.FC<{ review: ReviewDTO }> = (props) => {
    // Format the review date (assuming the backend provides a valid date format)
    const date = new Date(); // Placeholder since `ReviewDTO` doesn't include `date`
    const longMonth = date.toLocaleString("en-us", { month: "long" });
    const dateDay = date.getDate();
    const dateYear = date.getFullYear();
    const dateRender = `${longMonth} ${dateDay}, ${dateYear}`;

    return (
        <div>
            <div className="col-sm-8 col-md-8">
                <h5>User ID: {props.review.userId}</h5>
                <div className="row">
                    <div className="col">{dateRender}</div>
                    <div className="col">
                        <StarsReview rating={props.review.rating} size={16} />
                    </div>
                </div>
                <div className="mt-2">
                    <p>
                        <strong>Brewing Method:</strong> {props.review.brewingMethod}
                    </p>
                    <p>{props.review.content}</p>
                    <p>
                        <strong>Brewing Description:</strong> {props.review.brewingDescription}
                    </p>
                </div>
            </div>
            <hr />
        </div>
    );
};