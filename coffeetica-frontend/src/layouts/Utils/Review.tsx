import React from "react";
import { ReviewDTO } from "../../models/ReviewDTO";
import { StarsDisplay } from "./StarsDisplay";


export const Review: React.FC<{ review: ReviewDTO }> = ({ review }) => {

    const date = new Date(review.createdAt);
    const longMonth = date.toLocaleString("en-us", { month: "long" });
    const dateDay = date.getDate();
    const dateYear = date.getFullYear();
    const formattedDate = `${longMonth} ${dateDay}, ${dateYear}`;

    return (
        <div className="review-container py-3 border-bottom">
            <div className="row align-items-center mb-2">
                <div className="col">
                    <h5 className="mb-1">{review.userName}</h5>
                </div>
                <div className="col text-end text-muted small">{formattedDate}</div>
            </div>

            <div className="row align-items-center mb-2">
                <div className="col">
                    <StarsDisplay rating={review.rating} size={16} />
                </div>
            </div>

            <div className="review-content mt-2">
                <p>
                    <strong>Brewing Method:</strong> {review.brewingMethod}
                </p>
                <p>{review.content}</p>
                <p>
                    <strong>Brewing Description:</strong> {review.brewingDescription}
                </p>
            </div>
        </div>
    );
};