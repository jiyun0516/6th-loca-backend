package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// 사용자 생성 장소
@Entity
@Table(name = "custom_places")
@DiscriminatorValue("CUSTOM")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class CustomPlace extends Place {

    // 소유 사용자 (FK users.user_id)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "lat", nullable = false)
    private BigDecimal lat;

    @Column(name = "lng", nullable = false)
    private BigDecimal lng;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;
}
