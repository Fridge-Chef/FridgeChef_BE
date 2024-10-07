package Fridge_Chef.team.comment.rest.response;

import Fridge_Chef.team.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String comment;
    private double star;
    private int like;
    private String userName;
    private List<String> imageLink;
    private Long boardId;
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getStar(),
                comment.getTotalHit(),
                comment.getUser().getUsername(),
                comment.getImageLinks(),
                comment.getBoard().getId(),
                comment.getCreateTime()
        );
    }
}
