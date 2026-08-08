package gdg.hongik.loca.service;

import gdg.hongik.loca.entity.PlacePreference;
import gdg.hongik.loca.entity.UserPreference;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 사용자와 장소의 태그 비율을 비교해 ForYou 매칭 점수 계산
@Component
public class ForYouScoreCalculator {

    private static final int SCORE_SCALE = 6;

    public Map<Integer, BigDecimal> calculate(
            List<UserPreference> userPreferences,
            List<PlacePreference> placePreferences
    ) {
        if (userPreferences.isEmpty() || placePreferences.isEmpty()) {
            return Map.of();
        }

        Map<Integer, BigDecimal> normalizedUserScores =
                normalizeUserScores(userPreferences);

        Map<Integer, List<PlacePreference>> preferencesByPlace =
                placePreferences.stream()
                        .collect(Collectors.groupingBy(
                                PlacePreference::getPlaceId
                        ));

        Map<Integer, BigDecimal> scoresByPlace = new HashMap<>();

        preferencesByPlace.forEach((placeId, preferences) -> {
            BigDecimal placeTotal = preferences.stream()
                    .map(PlacePreference::getScore)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (placeTotal.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }

            BigDecimal matchScore = preferences.stream()
                    .map(preference -> {
                        BigDecimal userScore =
                                normalizedUserScores.get(
                                        preference.getTagId()
                                );

                        if (userScore == null) {
                            return BigDecimal.ZERO;
                        }

                        BigDecimal normalizedPlaceScore =
                                preference.getScore().divide(
                                        placeTotal,
                                        SCORE_SCALE,
                                        RoundingMode.HALF_UP
                                );

                        return userScore.multiply(normalizedPlaceScore);
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(SCORE_SCALE, RoundingMode.HALF_UP);

            if (matchScore.compareTo(BigDecimal.ZERO) > 0) {
                scoresByPlace.put(placeId, matchScore);
            }
        });

        return scoresByPlace;
    }

    private Map<Integer, BigDecimal> normalizeUserScores(
            List<UserPreference> preferences
    ) {
        BigDecimal userTotal = preferences.stream()
                .map(UserPreference::getScore)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (userTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return Map.of();
        }

        return preferences.stream()
                .collect(Collectors.toMap(
                        UserPreference::getTagId,
                        preference -> preference.getScore().divide(
                                userTotal,
                                SCORE_SCALE,
                                RoundingMode.HALF_UP
                        )
                ));
    }
}