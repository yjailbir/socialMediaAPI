package program.api.socialapi.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "friendships")
@Data
@ApiModel(description = "A pair of user id in which both users are friends of each other")
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user1_id")
    @ApiModelProperty(value = "friend1 id")
    private Integer user1_id;

    @Column(name = "user2_id")
    @ApiModelProperty(value = "friend2 id")
    private Integer user2_id;
}
