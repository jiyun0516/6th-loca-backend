package gdg.hongik.loca.dto.preference;

// 태그별 집계 점수 프로젝션 (선호도 재집계용)
// - score = count 결과(long)
public interface TagScoreProjection {

    Integer getTagId();

    Long getScore();
}
