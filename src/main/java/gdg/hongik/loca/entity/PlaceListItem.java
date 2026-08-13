package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

// 장소 목록 항목
// - 담기/빼기는 하드 삭제
// - placeId 는 places(부모) 참조. 타입 무관하게 가리키기 위한 통합 채번(#37)의 사용처
@Entity
@Table(name = "place_list_items")
@IdClass(PlaceListItemId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlaceListItem {

    @Id
    @Column(name = "list_id")
    private Long listId;

    @Id
    @Column(name = "place_id")
    private Integer placeId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
