package gdg.hongik.loca.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

import gdg.hongik.loca.enums.CompanionType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewCreateRequestDto {

    // 방문한 장소 ID (엔티티 place_id, not-null)
    private Integer placeId;

    // 오늘의 제목
    private String title;

    // 누구와 다녀왔나요? (ALONE, FRIEND, LOVER, FAMILY, ETC)
    private CompanionType companion;

    // 오늘의 키워드
    private List<String> keywords;

    // 분위기 태그
    private List<String> atmosphereTags;

    // 사진 URL
    private List<String> imageUrls;
}
