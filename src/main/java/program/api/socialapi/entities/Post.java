package program.api.socialapi.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "posts")
@Data
@ApiModel(description = "The information that the user posts to the feed")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    @ApiModelProperty(value = "Title of the post")
    private String title;

    @Column(name = "text")
    @ApiModelProperty(value = "Main text of the post")
    private String text;

    @Column(name = "image")
    @ApiModelProperty(value = "Image attached to the post")
    private byte[] image;

    @Column(name = "user_id")
    @ApiModelProperty(value = "Id of post author")
    private Integer user_id;

    @Column(name = "created_at")
    @ApiModelProperty(value = "Date of post creation")
    private Date created_at;
}
