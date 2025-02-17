import React, { useContext, useState } from "react";
import { CoffeeDTO } from "../../../../models/CoffeeDTO";
import { ReviewRequestDTO } from "../../../../models/ReviewRequestDTO";
import { ReviewDTO } from "../../../../models/ReviewDTO";
import { AuthContext } from "../../../../auth/AuthContext";
import { Link } from "react-router-dom";
import { ReviewForm } from "./ReviewForm";
import { ReviewDisplay } from "./ReviewDisplay";

interface ReviewBoxProps {
    coffee: CoffeeDTO | undefined;
    userReview: ReviewDTO | null;
    createReview: (reviewData: ReviewRequestDTO) => void;
    updateReview: (reviewId: number, updatedReview: ReviewRequestDTO) => void;
    deleteReview: (reviewId: number) => void;
}

export const ReviewBox: React.FC<ReviewBoxProps> = ({
    coffee,
    userReview,
    createReview,
    updateReview,
    deleteReview,
}) => {
    const { isAuthenticated } = useContext(AuthContext);
    const [isEditing, setIsEditing] = useState(false);

    if (!coffee?.id) {
        return null;
    }

    // Unauthenticated user → show message
    if (!isAuthenticated) {
        return (
            <div className="card col-3 container d-flex mb-5">
                <div className="card-body container">
                    <h4 className="text-success">Leave a review</h4>
                    <hr />
                    <p>Sign in to be able to leave a review.</p>
                    <Link to="/login" className="btn btn-primary">
                        Sign in
                    </Link>
                </div>
            </div>
        );
    }

    // No existing review → allow user to create one (POST)
    if (!userReview) {
        return (
            <div className="card col-3 container d-flex mb-5">
                <div className="card-body container">
                    <h4 className="text-success">Add a Review</h4>
                    <hr />
                    <ReviewForm
                        coffeeId={coffee.id}
                        onSubmit={(data) => {
                            createReview(data);
                        }}
                    />
                </div>
            </div>
        );
    }

    // User is editing an existing review (PUT)
    if (isEditing && userReview) {
        return (
            <div className="card col-3 container d-flex mb-5">
                <div className="card-body container">
                    <h4 className="text-success">Edit Your Review</h4>
                    <hr />
                    <ReviewForm
                        coffeeId={coffee.id}
                        initialReview={userReview}
                        onSubmit={(formData) => {
                            // Create an object for the PUT request, including the review ID
                            const updatedReview: ReviewRequestDTO = {
                                coffeeId: coffee.id,
                                rating: formData.rating,
                                content: formData.content,
                                brewingMethod: formData.brewingMethod,
                                brewingDescription: formData.brewingDescription,
                            };
                            updateReview(userReview.id, updatedReview);
                            setIsEditing(false);
                        }}
                        onCancel={() => setIsEditing(false)}
                    />
                </div>
            </div>
        );
    }

    // Existing review → display it
    return (
        <div className="card col-3 container d-flex mb-5">
            <div className="card-body container">
                <ReviewDisplay
                    review={userReview}
                    onEdit={() => setIsEditing(true)}
                    onDelete={() => deleteReview(userReview.id)}
                />
            </div>
        </div>
    );
};