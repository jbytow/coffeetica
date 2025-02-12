import { useContext, useState } from "react";
import { StarsSelector } from "./StarsSelector";
import { AuthContext } from "../../auth/AuthContext";
import { ReviewRequestDTO } from "../../models/ReviewRequestDTO";

export const LeaveAReview: React.FC<{
    submitReview: (data: ReviewRequestDTO) => void;
    coffeeId: number;
}> = ({ submitReview, coffeeId }) => {
    const { user } = useContext(AuthContext);
    const [starInput, setStarInput] = useState(0);
    const [reviewDescription, setReviewDescription] = useState("");
    const [brewingMethod, setBrewingMethod] = useState("");
    const [brewingDescription, setBrewingDescription] = useState("");

    function starValue(value: number) {
        setStarInput(value);
    }

    return (
        <div style={{ cursor: "pointer" }}>
            {/* Review rating component */}
            <div className="mt-2">
                <StarsSelector rating={starInput} onRatingChange={starValue} />
            </div>

            {/* Always visible review form */}
            <form method="POST" action="#">
                <hr />

                {/* Brewing Method */}
                <div className="mb-3">
                    <label className="form-label">Brewing Method</label>
                    <input
                        type="text"
                        className="form-control"
                        placeholder="e.g. French Press, Espresso"
                        value={brewingMethod}
                        onChange={(e) => setBrewingMethod(e.target.value)}
                        required
                    />
                </div>

                {/* Brewing Description */}
                <div className="mb-3">
                    <label className="form-label">Brewing Description (optional)</label>
                    <textarea
                        className="form-control"
                        placeholder="Describe your brewing process..."
                        rows={2}
                        value={brewingDescription}
                        onChange={(e) => setBrewingDescription(e.target.value)}
                    />
                </div>

                {/* Review Description */}
                <div className="mb-3">
                    <label className="form-label">Review Description</label>
                    <textarea
                        className="form-control"
                        placeholder="Write your review here..."
                        rows={3}
                        value={reviewDescription}
                        onChange={(e) => setReviewDescription(e.target.value)}
                        required
                    />
                </div>

                <div>
                    <button
                        type="button"
                        onClick={() => {
                            const reviewData: ReviewRequestDTO = {
                                coffeeId,
                                rating: starInput,
                                content: reviewDescription,
                                brewingMethod,
                                brewingDescription,
                            };
                            submitReview(reviewData);
                        }}
                        className="btn btn-primary mt-3"
                    >
                        Submit Review
                    </button>
                </div>
            </form>
        </div>
    );
};