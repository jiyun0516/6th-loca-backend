package gdg.hongik.loca.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PlaceListItemId implements Serializable {
    private Long listId;
    private Integer placeId;
}
