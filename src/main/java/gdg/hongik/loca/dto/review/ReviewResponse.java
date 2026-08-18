package gdg.hongik.loca.dto.review;

import gdg.hongik.loca.entity.VisitRecord;
import gdg.hongik.loca.enums.CompanionType;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

// 방문 후기 응답 DTO
public record ReviewResponse(
        Long reviewId,
        Integer placeId,

        // 장소 타입 ('PUBLIC' | 'CUSTOM')
        // - 프론트가 장소 상세 경로를 고르는 데 필요
        String placeType,

        String title,
        String content,
        CompanionType companion,
        List<String> keywords,

        // 선택 태그 ID 목록 (visit_tags)
        List<Integer> tagIds,

        List<String> imageUrls,
        OffsetDateTime visitedAt,
        OffsetDateTime createdAt
) {

    // 엔티티 -> 응답 DTO 매핑
    // - reviewId <- visitId
    // - placeId: 엔티티와 동일 Integer (변환 없음)
    // - placeType: 엔티티에 없는 값이라 서비스에서 주입 (public_places 존재 여부로 판정)
    // - keywords: Set -> List
    // - tagIds: visit_tags 조회 결과를 서비스에서 주입 (엔티티 연관 매핑 없음)
    // - visitedAt/createdAt: OffsetDateTime 그대로 (타임존 보존)
    // - 컬렉션에 null 분기를 두지 않음: Hibernate가 로드 시 항상 컬렉션을 할당하고
    //   생성 경로도 빈 컬렉션을 넣으므로 null이 들어올 수 없음
    public static ReviewResponse from(VisitRecord record, List<Integer> tagIds, String placeType) {
        return new ReviewResponse(
                record.getVisitId(),
                record.getPlaceId(),
                placeType,
                record.getTitle(),
                record.getContent(),
                record.getCompanion(),
                new ArrayList<>(record.getKeywords()),
                tagIds,
                new ArrayList<>(record.getImageUrls()),
                record.getVisitedAt(),
                record.getCreatedAt()
        );
    }
}
