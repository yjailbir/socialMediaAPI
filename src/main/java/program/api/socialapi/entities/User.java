package program.api.socialapi.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "users")
@Data
@ApiModel(description = "User of this social media")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    @ApiModelProperty(value = "User's username")
    private String username;

    @Column(name = "email")
    @ApiModelProperty(value = "User's email")
    private String email;

    @Column(name = "password")
    @ApiModelProperty(value = "User's password")
    private String password;
}
