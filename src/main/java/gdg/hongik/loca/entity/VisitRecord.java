package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

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

    @Column(name = "rating")
    private Short rating;

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "visited_at", nullable = false)
    private OffsetDateTime visitedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
