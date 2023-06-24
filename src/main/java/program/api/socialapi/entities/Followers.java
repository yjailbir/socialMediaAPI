package program.api.socialapi.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "followers")
@Data
@ApiModel(description = "A pair of user id in which the sender is subscribed to the recipient")
public class Followers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender_id")
    @ApiModelProperty(value = "subscriber's id")
    private Integer sender_id;

    @Column(name = "receiver_id")
    @ApiModelProperty(value = "subscribe recipient id")
    private Integer receiver_id;
}
