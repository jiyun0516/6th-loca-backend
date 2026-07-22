package gdg.hongik.loca.entity;

import gdg.hongik.loca.enums.CompanionType;
import gdg.hongik.loca.enums.MoodType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "visit_records")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VisitRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long visitId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "place_id", nullable = false)
    private Integer placeId;

    // 방문 후기 제목
    @Column(name = "title", columnDefinition = "text")
    private String title;

    @Column(name = "rating")
    private Short rating;

    // 동행 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "companion")
    private CompanionType companion;

    // 방문 당시 감정
    @Enumerated(EnumType.STRING)
    @Column(name = "mood")
    private MoodType mood;

    // 기억에 남는 순간
    @Column(name = "memorable_moment", columnDefinition = "text")
    private String memorableMoment;

    // 좋았던 점
    @Column(name = "good_point", columnDefinition = "text")
    private String goodPoint;

    // 가격(원)
    @Column(name = "price")
    private Integer price;

    // 가격 모름 여부
    @Column(name = "unknown_price")
    private Boolean unknownPrice;

    // 미래의 나에게 남기는 메시지
    @Column(name = "message_to_future", columnDefinition = "text")
    private String messageToFuture;

    // 키워드 목록 (visit_keywords)
    @ElementCollection
    @CollectionTable(name = "visit_keywords", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "keyword")
    @Builder.Default
    private Set<String> keywords = new LinkedHashSet<>();

    // 분위기 태그 목록 (visit_atmosphere_tags)
    @ElementCollection
    @CollectionTable(name = "visit_atmosphere_tags", joinColumns = @JoinColumn(name = "visit_id"))
    @Column(name = "atmosphere_tag")
    @Builder.Default
    private Set<String> atmosphereTags = new LinkedHashSet<>();

    // 이미지 URL 목록 (visit_images, 정렬 순서 보존)
    @ElementCollection
    @CollectionTable(name = "visit_images", joinColumns = @JoinColumn(name = "visit_id"))
    @OrderColumn(name = "sort_order")
    @Column(name = "image_url")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "visited_at", nullable = false)
    private OffsetDateTime visitedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
