import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { ReviewDTO } from "../../../../models/ReviewDTO";
import apiClient from "../../../../lib/api";
import { SpinnerLoading } from "../../../Utils/SpinnerLoading";
import { Review } from "../../../Utils/Review";
import { Pagination } from "../../../Utils/Pagination";

export const ReviewListPage = () => {
    const { coffeeId } = useParams<{ coffeeId: string }>();

    const [reviews, setReviews] = useState<ReviewDTO[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [httpError, setHttpError] = useState<string | null>(null);

    // Pagination and sorting
    const [currentPage, setCurrentPage] = useState(1);
    const [reviewsPerPage] = useState(3);
    const [totalAmountOfReviews, setTotalAmountOfReviews] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    // Single dropdown for sorting
    const [sortOption, setSortOption] = useState("createdAt_desc"); // Default: Newest first

    // Detect if the user is on a mobile device
    const isMobile = window.innerWidth <= 768;
    const sectionMargin = isMobile ? "mt-2 mb-3" : "mt-3 mb-4";

    useEffect(() => {
        const fetchReviews = async () => {
            if (!coffeeId) return;

            setIsLoading(true);

            // Extract sorting parameters from the selected option
            const [sortBy, sortDirection] = sortOption.split("_");

            try {
                const response = await apiClient.get(`/reviews`, {
                    params: {
                        coffeeId,
                        page: currentPage - 1,
                        size: reviewsPerPage,
                        sortBy,
                        direction: sortDirection,
                    },
                });

                setReviews(response.data.content);
                setTotalAmountOfReviews(response.data.totalElements);
                setTotalPages(response.data.totalPages);
            } catch (error: any) {
                setHttpError(error.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchReviews();
    }, [coffeeId, currentPage, sortOption]);

    if (isLoading) {
        return <SpinnerLoading />;
    }

    if (httpError) {
        return (
            <div className="container m-5">
                <p className="alert alert-danger">{httpError}</p>
            </div>
        );
    }

    const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

    // Calculate the currently displayed range of reviews
    const indexOfLastReview = currentPage * reviewsPerPage;
    const indexOfFirstReview = indexOfLastReview - reviewsPerPage;
    const lastItem = indexOfLastReview <= totalAmountOfReviews ? indexOfLastReview : totalAmountOfReviews;

    return (
        <section className={`container ${sectionMargin}`}>
            {/* Page header */}
            <div className="row">
                <div className="col-12">
                    <h2 className="mt-5 mb-4">All Reviews ({totalAmountOfReviews})</h2>
                </div>
            </div>

            {/* Sorting dropdown */}
            <div className="row mb-4">
                <div className="col-md-4">
                    <label htmlFor="sortOption" className="form-label">
                        Sort reviews by:
                    </label>
                    <select
                        id="sortOption"
                        className="form-select"
                        value={sortOption}
                        onChange={(e) => {
                            setSortOption(e.target.value);
                            setCurrentPage(1);
                        }}
                    >
                        <option value="createdAt_desc">Newest</option>
                        <option value="rating_desc">Highest Rating</option>
                        <option value="rating_asc">Lowest Rating</option>
                    </select>
                </div>
            </div>

            {/* Display the current range of reviews */}
            <p className="text-muted">
                Showing {indexOfFirstReview + 1} to {lastItem} of {totalAmountOfReviews} reviews:
            </p>

            {/* Reviews list */}
            <div className="row">
                {reviews.length > 0 ? (
                    reviews.map((review) => (
                        <div className="col-12 mb-3" key={review.id}>
                            <Review review={review} />
                        </div>
                    ))
                ) : (
                    <div className="col-12">
                        <p className="lead text-muted">There are no reviews for this coffee yet.</p>
                    </div>
                )}
            </div>

            <hr />

            {/* Pagination */}
            {totalPages > 1 && (
                <div className="d-flex justify-content-center mt-3">
                    <Pagination currentPage={currentPage} totalPages={totalPages} paginate={paginate} />
                </div>
            )}
        </section>
    );
};