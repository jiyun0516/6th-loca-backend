package gdg.hongik.loca.dto.review;

import gdg.hongik.loca.enums.CompanionType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.List;

// 방문 후기 수정 요청 DTO
// - 모든 필드는 null이면 기존 값 유지
// - 컬렉션은 null이면 유지, []이면 전체 삭제
public record ReviewUpdateRequest(

        // 오늘의 제목
        String title,

        // 후기 본문
        String content,

        // 누구와 다녀왔나요? (ALONE, FRIEND, LOVER, FAMILY, ETC)
        CompanionType companion,

        // 방문 일시
        OffsetDateTime visitedAt,

        // 오늘의 키워드 (자유 입력, 표시용)
        // - 해시태그 형식: 공백 불가
        List<@Pattern(regexp = "\\S+", message = "키워드에 공백을 포함할 수 없습니다") String> keywords,

        // 선택 태그 ID 목록 (추천 점수용, visit_tags)
        // - 최대 5개
        // - 같은 리뷰 안의 중복은 하나로 합쳐짐
        // - 미존재 tagId -> 404
        @Size(max = 5, message = "태그는 최대 5개까지 선택할 수 있습니다")
        List<Integer> tagIds,

        // 사진 URL
        List<String> imageUrls
) {
}
