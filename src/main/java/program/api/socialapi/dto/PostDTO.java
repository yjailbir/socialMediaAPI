package program.api.socialapi.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PostDTO {
    String author;
    private Date created_at;
    private String title;
    private String text;
    private byte[] image;
}
