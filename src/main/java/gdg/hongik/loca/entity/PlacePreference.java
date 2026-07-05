package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "place_preferences")
@IdClass(PlacePreferenceId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlacePreference {

    @Id
    @Column(name = "place_id")
    private Integer placeId;

    @Id
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "score", nullable = false)
    private BigDecimal score;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
