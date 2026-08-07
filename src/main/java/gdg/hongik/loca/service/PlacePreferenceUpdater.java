package gdg.hongik.loca.service;

import gdg.hongik.loca.repository.PublicPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

// 장소 태그 점수 재집계 조율
// - 증분 아님. 해당 장소 전체를 다시 계산해 덮어씀
// - 집계 함수 = count(distinct user_id) (한 사람은 한 표)
// - 갱신 시점 = 추천 조회 시, dirty 장소만 (쓰기 시점 갱신은 사용자 간 경합으로 행 잠금 필요)
// - dirty 표시는 public_places.preference_dirty_at 컬럼. 재시작/다중 인스턴스에 무관
@Service
@RequiredArgsConstructor
public class PlacePreferenceUpdater {

    private final PublicPlaceRepository publicPlaceRepository;
    private final PlacePreferenceRefresher placePreferenceRefresher;

    // 재집계 대상 표시 (리뷰 생성/수정/삭제 시 호출)
    // - 호출부(리뷰 쓰기) 트랜잭션에 참여해야 하므로 트랜잭션 어노테이션을 두지 않음
    public void markDirty(Integer placeId) {
        if (placeId != null) {
            publicPlaceRepository.markPreferenceDirty(placeId, OffsetDateTime.now());
        }
    }

    // dirty 장소 일괄 재집계 (추천 조회 전 호출)
    // - @Transactional을 붙이지 말 것: 붙이면 장소별 트랜잭션 격리가 흐려짐
    public void refreshDirty() {
        List<Integer> targets = publicPlaceRepository.findDirtyPlaceIds();
        targets.forEach(placePreferenceRefresher::refreshOne);
    }
}
