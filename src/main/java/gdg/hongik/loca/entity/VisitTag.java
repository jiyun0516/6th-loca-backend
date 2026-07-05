package gdg.hongik.loca.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "visit_tags")
@IdClass(VisitTagId.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class VisitTag {

    @Id
    @Column(name = "visit_id")
    private Long visitId;

    @Id
    @Column(name = "tag_id")
    private Integer tagId;
}
