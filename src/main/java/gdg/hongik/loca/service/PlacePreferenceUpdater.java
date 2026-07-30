package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.preference.TagScoreProjection;
import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.repository.PlacePreferenceRepository;
import gdg.hongik.loca.repository.VisitTagRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// 장소 태그 점수 재집계
// - 증분 아님. 해당 장소 전체를 다시 계산해 덮어씀
// - 집계 함수 = count(distinct user_id) (한 사람은 한 표)
// - 갱신 시점 = 추천 조회 시, dirty 장소만 (쓰기 시점 갱신은 사용자 간 경합으로 행 잠금 필요)
// - dirty 목록은 인메모리 보관: 단일 인스턴스(Render) 전제. 재시작 시 유실되므로
//   유실분은 다음 리뷰 쓰기로 다시 dirty 처리됨
@Service
@RequiredArgsConstructor
public class PlacePreferenceUpdater {

    private final PlacePreferenceRepository placePreferenceRepository;
    private final VisitTagRepository visitTagRepository;
    private final EntityManager entityManager;

    private final Set<Integer> dirtyPlaceIds = ConcurrentHashMap.newKeySet();

    // 재집계 대상 표시 (리뷰 생성/수정/삭제 시 호출)
    public void markDirty(Integer placeId) {
        if (placeId != null) {
            dirtyPlaceIds.add(placeId);
        }
    }

    // dirty 장소 일괄 재집계 (추천 조회 전 호출)
    // - REQUIRES_NEW: 호출부(추천 조회)가 readOnly 트랜잭션이므로 쓰기용 트랜잭션 분리
    // - 표시 해제를 먼저 하여 재집계 중 들어온 쓰기가 다음 회차에 잡히도록 함
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refreshDirty() {
        if (dirtyPlaceIds.isEmpty()) {
            return;
        }
        List<Integer> targets = List.copyOf(dirtyPlaceIds);
        dirtyPlaceIds.removeAll(targets);
        targets.forEach(this::refreshOne);
    }

    // 장소 단건 재집계
    // - flush 선행: 방금 저장한 visit_tags가 집계 쿼리에 보이도록
    // - 기존 행 전체 삭제 후 집계 결과 재삽입
    private void refreshOne(Integer placeId) {
        entityManager.flush();

        List<TagScoreProjection> scores = visitTagRepository.aggregateByPlaceId(placeId);

        placePreferenceRepository.deleteByPlaceId(placeId);

        List<PlacePreference> rows = scores.stream()
                .map(s -> PlacePreference.builder()
                        .placeId(placeId)
                        .tagId(s.getTagId())
                        .score(BigDecimal.valueOf(s.getScore()))
                        .build())
                .toList();
        placePreferenceRepository.saveAll(rows);
    }
}
