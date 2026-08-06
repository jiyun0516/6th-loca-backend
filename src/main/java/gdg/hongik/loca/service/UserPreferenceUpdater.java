package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.preference.TagScoreProjection;
import gdg.hongik.loca.entity.UserPreference;
import gdg.hongik.loca.repository.UserPreferenceRepository;
import gdg.hongik.loca.repository.VisitTagRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

// 사용자 취향 점수 재집계
// - 증분(+1/-1) 아님. 해당 사용자 전체를 다시 계산해 덮어씀
// - 집계 함수 = count(*) (태그 반복 선택이 취향 강도)
// - 갱신 시점 = 리뷰 생성/수정/삭제 (본인 행만 건드려 잠금 불필요)
@Service
@RequiredArgsConstructor
public class UserPreferenceUpdater {

    private final UserPreferenceRepository userPreferenceRepository;
    private final VisitTagRepository visitTagRepository;
    private final EntityManager entityManager;

    // 사용자 취향 점수 재계산
    // - flush 선행: 방금 저장한 visit_tags가 집계 쿼리에 보이도록 (누락 시 조용히 틀림)
    // - 기존 행 전체 삭제 후 집계 결과 재삽입
    @Transactional
    public void refresh(Integer userId) {
        entityManager.flush();

        List<TagScoreProjection> scores = visitTagRepository.findTagScoresByUserId(userId);

        userPreferenceRepository.deleteByUserId(userId);

        List<UserPreference> rows = scores.stream()
                .map(s -> UserPreference.builder()
                        .userId(userId)
                        .tagId(s.getTagId())
                        .score(BigDecimal.valueOf(s.getScore()))
                        .build())
                .toList();
        userPreferenceRepository.saveAll(rows);
    }
}
