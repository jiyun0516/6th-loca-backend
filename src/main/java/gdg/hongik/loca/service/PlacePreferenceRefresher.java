package gdg.hongik.loca.service;

import gdg.hongik.loca.dto.preference.TagScoreProjection;
import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.PublicPlace;
import gdg.hongik.loca.repository.PlacePreferenceRepository;
import gdg.hongik.loca.repository.PublicPlaceRepository;
import gdg.hongik.loca.repository.VisitTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

// 장소 단건 재집계
@Service
@RequiredArgsConstructor
public class PlacePreferenceRefresher {

    private final PlacePreferenceRepository placePreferenceRepository;
    private final PublicPlaceRepository publicPlaceRepository;
    private final VisitTagRepository visitTagRepository;

    // - 장소별 독립 트랜잭션. 1건 실패가 다른 장소에 번지지 않음
    // - 잠금 조회로 소유권 주장. 표시가 이미 지워졌으면 앞선 요청이 끝낸 것이라 건너뜀
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void refreshOne(Integer placeId) {
        Optional<PublicPlace> claimed = publicPlaceRepository.findByPlaceIdForUpdate(placeId);
        if (claimed.isEmpty() || claimed.get().getPreferenceDirtyAt() == null) {
            return;
        }

        List<TagScoreProjection> scores = visitTagRepository.findTagScoresByPlaceId(placeId);

        placePreferenceRepository.deleteByPlaceId(placeId);

        List<PlacePreference> rows = scores.stream()
                .map(s -> PlacePreference.builder()
                        .placeId(placeId)
                        .tagId(s.getTagId())
                        .score(BigDecimal.valueOf(s.getScore()))
                        .build())
                .toList();
        placePreferenceRepository.saveAll(rows);

        claimed.get().setPreferenceDirtyAt(null);
    }
}
