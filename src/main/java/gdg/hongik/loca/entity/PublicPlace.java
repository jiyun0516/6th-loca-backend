package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

// 공개 장소
@Entity
@Table(name = "public_places")
@DiscriminatorValue("PUBLIC")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class PublicPlace extends Place {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "kakao_place_id", nullable = false, unique = true)
    private String kakaoPlaceId;

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

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "preference_dirty_at")
    private OffsetDateTime preferenceDirtyAt;
}
