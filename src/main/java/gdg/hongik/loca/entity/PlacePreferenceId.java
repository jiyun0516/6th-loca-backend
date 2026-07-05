package gdg.hongik.loca.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlacePreferenceId implements Serializable {
    private Integer placeId;
    private Integer tagId;
}
