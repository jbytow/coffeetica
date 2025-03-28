import React from "react";

interface StarsDisplayProps {
    rating: number;
    size?: number;
}


export const StarsDisplay: React.FC<StarsDisplayProps> = ({
    rating,
    size = 32,
  }) => {
    return (
      <div style={{ display: "inline-flex", verticalAlign: "middle" }}>
        {Array.from({ length: 5 }, (_, i) => {
          const isFull = i + 1 <= Math.floor(rating);
          const isHalf = i + 0.5 === rating;
  
          return (
            <svg
              key={i}
              width={size}
              height={size}
              viewBox="0 0 16 16"
              xmlns="http://www.w3.org/2000/svg"
              style={{ marginRight: i < 4 ? 2 : 0 }} 
            >
              <defs>
                <clipPath id={`halfStar-${i}`}>
                  <rect x="0" y="0" width="8" height="16" />
                </clipPath>
              </defs>
  
              {/* Star backvground (gray) */}
              <path
                fill="#d3d3d3"
                d="M3.612 15.443c-.386.198-.824-.149-.746-.592l
                   .83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696 
                   L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95
                   l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
              />
  
              {/* Full star (gold) */}
              {isFull && (
                <path
                  fill="gold"
                  d="M3.612 15.443c-.386.198-.824-.149-.746-.592l
                     .83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696 
                     L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95
                     l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
                />
              )}
  
              {/* Half star (gold, with clip path) */}
              {isHalf && (
                <path
                  fill="gold"
                  d="M3.612 15.443c-.386.198-.824-.149-.746-.592l
                     .83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696 
                     L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95
                     l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"
                  clipPath={`url(#halfStar-${i})`}
                />
              )}
            </svg>
          );
        })}
      </div>
    );
  };