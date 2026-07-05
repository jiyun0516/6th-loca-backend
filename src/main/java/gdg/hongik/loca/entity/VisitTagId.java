package gdg.hongik.loca.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VisitTagId implements Serializable {
    private Long visitId;
    private Integer tagId;
}
