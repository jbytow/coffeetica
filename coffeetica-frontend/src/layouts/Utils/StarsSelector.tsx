import React, { useState } from "react";

interface StarsSelectorProps {
  rating: number;
  onRatingChange: (rating: number) => void;
  size?: number;
}

export const StarsSelector: React.FC<StarsSelectorProps> = ({
  rating,
  onRatingChange,
  size = 32,
}) => {
  // Local state for handling hover effects
  const [hoverRating, setHoverRating] = useState<number | null>(null);

  // The displayed rating (either the hovered value or the actual rating)
  const displayedRating = hoverRating !== null ? hoverRating : rating;

  return (
    <div
      style={{ display: "inline-flex", cursor: "pointer" }}
      onMouseLeave={() => setHoverRating(null)} // Reset hover effect when mouse leaves
    >
      {Array.from({ length: 5 }, (_, i) => {
        // Calculate if the star should be full, half, or empty
        const isFull = i + 1 <= Math.floor(displayedRating);
        const isHalf = i + 0.5 === displayedRating;

        return (
          <div
            key={i}
            style={{
              position: "relative",
              width: size,
              height: size,
              cursor: "pointer",
            }}
          >
            {/* Left half of the star - hover sets rating to i + 0.5 */}
            <div
              style={{
                position: "absolute",
                left: 0,
                top: 0,
                width: size / 2,
                height: size,
              }}
              onMouseEnter={() => setHoverRating(i + 0.5)}
              onClick={() => onRatingChange(i + 0.5)}
            />

            {/* Right half of the star - hover sets rating to i + 1 */}
            <div
              style={{
                position: "absolute",
                right: 0,
                top: 0,
                width: size / 2,
                height: size,
              }}
              onMouseEnter={() => setHoverRating(i + 1)}
              onClick={() => onRatingChange(i + 1)}
            />

            <svg
              width={size}
              height={size}
              viewBox="0 0 16 16"
              xmlns="http://www.w3.org/2000/svg"
            >
              <defs>
                {/* Clip path to mask half of the star */}
                <clipPath id={`halfStar-${i}`}>
                  <rect x="0" y="0" width="8" height="16" />
                </clipPath>
              </defs>

              {/* Full star background (light gray) */}
              <path
                fill="#d3d3d3" // Softer gray instead of dark gray
                d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
              />

              {/* Full star overlay (gold) */}
              {isFull && (
                <path
                  fill="gold"
                  d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
                />
              )}

              {/* Half star overlay (gold, clipped) */}
              {isHalf && (
                <path
                  fill="gold"
                  d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
                  clipPath={`url(#halfStar-${i})`}
                />
              )}
            </svg>
          </div>
        );
      })}
    </div>
  );
};