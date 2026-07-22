package gdg.hongik.loca.dto.recommendation;

import java.math.BigDecimal;

// Explore 점수 집계 프로젝션
// - placeId: 후보 장소 식별자
// - score: 선택 태그 점수 합계
public interface PlaceScoreProjection {

    Integer getPlaceId();

    BigDecimal getScore();
}
