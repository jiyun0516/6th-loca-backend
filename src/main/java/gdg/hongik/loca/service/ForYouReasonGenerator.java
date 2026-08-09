package gdg.hongik.loca.service;

import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.UserPreference;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// 사용자와 장소의 공통 선호 태그를 기준으로 ForYou 추천 근거 생성
@Component
public class ForYouReasonGenerator {

    private static final int MATCHED_TAG_LIMIT = 3;

    public Result generate(
            List<UserPreference> userPreferences,
            List<PlacePreference> placePreferences,
            Map<Integer, String> tagNamesById
    ) {
        if (userPreferences.isEmpty() || placePreferences.isEmpty()) {
            return Result.empty();
        }

        Map<Integer, UserPreference> userPreferenceByTagId =
                userPreferences.stream()
                        .collect(Collectors.toMap(
                                UserPreference::getTagId,
                                Function.identity()
                        ));

        List<String> matchedTags = placePreferences.stream()
                // 사용자와 장소가 공통으로 가진 태그만 사용
                .filter(placePreference ->
                        userPreferenceByTagId.containsKey(
                                placePreference.getTagId()
                        ))
                // 추천 점수 기여도가 높은 태그부터 정렬
                .sorted(
                        Comparator
                                .comparing(
                                        (PlacePreference placePreference) ->
                                                calculateContribution(
                                                        userPreferenceByTagId.get(
                                                                placePreference.getTagId()
                                                        ),
                                                        placePreference
                                                ),
                                        Comparator.reverseOrder()
                                )
                                .thenComparing(PlacePreference::getTagId)
                )
                .limit(MATCHED_TAG_LIMIT)
                .map(PlacePreference::getTagId)
                .map(tagNamesById::get)
                .filter(tagName ->
                        tagName != null && !tagName.isBlank())
                .toList();

        return new Result(
                matchedTags,
                createReason(matchedTags)
        );
    }

    // 해당 태그가 추천 점수에 기여하는 정도
    private BigDecimal calculateContribution(
            UserPreference userPreference,
            PlacePreference placePreference
    ) {
        return userPreference.getScore()
                .multiply(placePreference.getScore());
    }

    private String createReason(List<String> matchedTags) {
        if (matchedTags.isEmpty()) {
            return "최근 기록을 반영한 추천이에요.";
        }

        if (matchedTags.size() == 1) {
            return "최근 기록과 비슷한 취향의 장소로 추천해요.";
        }

        String preferences = matchedTags.stream()
                .map(tagName -> "'" + tagName + "'")
                .collect(Collectors.joining(", "));

        return "최근 기록의 "
                + preferences
                + " 취향을 반영한 추천이에요.";
    }

    public record Result(
            List<String> matchedTags,
            String recommendationReason
    ) {

        public static Result empty() {
            return new Result(
                    List.of(),
                    "최근 기록을 반영한 추천이에요."
            );
        }
    }
}