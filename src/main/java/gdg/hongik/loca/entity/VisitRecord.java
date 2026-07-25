package gdg.hongik.loca.entity;

import gdg.hongik.loca.enums.CompanionType;
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

    // 방문 후기 본문
    @Column(name = "content", columnDefinition = "text")
    private String content;

    // 동행 유형
    @Enumerated(EnumType.STRING)
    @Column(name = "companion")
    private CompanionType companion;

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
