package gdg.hongik.loca.dto.recommendation;

public record ForYouStatusResponse(
        boolean unlocked,
        long reviewCount,
        int requiredReviewCount,
        long remainingReviewCount
) {
    public static ForYouStatusResponse of(
            long reviewCount,
            int requiredReviewCount
    ) {
        long remainingReviewCount =
                Math.max(0, requiredReviewCount - reviewCount);

        return new ForYouStatusResponse(
                remainingReviewCount == 0,
                reviewCount,
                requiredReviewCount,
                remainingReviewCount
        );
    }
}