package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

// 장소 목록
// - 하드 삭제. 목록을 지우면 place_list_items 도 DB cascade 로 함께 사라짐
// - shareToken / sharedAt 은 공유 PR 에서 사용. 컬럼만 먼저 확보
@Entity
@Table(name = "place_lists")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PlaceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "list_id")
    private Long listId;

    // 소유 사용자 (FK users.user_id)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "name", nullable = false)
    private String name;

    // 공유 상태는 이 컬럼 하나로 표현. null = 공유 안 함
    @Column(name = "share_token", unique = true)
    private String shareToken;

    @Column(name = "shared_at")
    private OffsetDateTime sharedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
