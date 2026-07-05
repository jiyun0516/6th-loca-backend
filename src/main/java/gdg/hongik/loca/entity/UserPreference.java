package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_preferences")
@IdClass(UserPreferenceId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "tag_id")
    private Integer tagId;

    @Column(name = "score", nullable = false)
    private BigDecimal score;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
