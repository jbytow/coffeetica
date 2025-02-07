import React from "react";

interface StarsDisplayProps {
    rating: number;
}

export const StarsDisplay: React.FC<StarsDisplayProps> = ({ rating }) => {
    return (
        <div style={{ display: "inline-flex", fontSize: "32px" }}>
            {[1, 2, 3, 4, 5].map((star) => (
                <span key={star} style={{ position: "relative", marginRight: "4px" }}>
                    {/* Pełna gwiazdka */}
                    {rating >= star ? (
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="gold" className="bi bi-star-fill" viewBox="0 0 16 16">
                            <path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
                        </svg>
                    ) : rating >= star - 0.5 ? (
                        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="gold" className="bi bi-star-half" viewBox="0 0 16 16">
                            <path d="M5.354 5.119 7.538.792A.52.52 0 0 1 8 .5c.183 0 .366.097.465.292l2.184 4.327 4.898.696A.54.54 0 0 1 16 6.32a.55.55 0 0 1-.17.445l-3.523 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z" />
                        </svg>
                    ) : (
                        <span className="bi bi-star"></span>
                    )}
                </span>
            ))}
        </div>
    );
};