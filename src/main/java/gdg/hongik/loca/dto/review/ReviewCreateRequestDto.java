package gdg.hongik.loca.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

import gdg.hongik.loca.enums.CompanionType;
import gdg.hongik.loca.enums.MoodType;

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

    // 오늘의 기분 (HAPPY, CALM ...)
    private MoodType mood;

    // 오늘의 키워드
    private List<String> keywords;

    // 기억에 남는 시간
    private String memorableMoment;

    // 좋았던 점
    private String goodPoint;

    // 만족도 (1~5)
    private Integer rating;

    // 사용 금액
    private Integer price;

    // 기억 안나요 체크
    private Boolean unknownPrice;

    // 분위기 태그
    private List<String> atmosphereTags;

    // 미래의 나에게 한마디
    private String messageToFuture;

    // 사진 URL
    private List<String> imageUrls;
}
