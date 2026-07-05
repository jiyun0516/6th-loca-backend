package gdg.hongik.loca.entity;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserPreferenceId implements Serializable {
    private Integer userId;
    private Integer tagId;
}
