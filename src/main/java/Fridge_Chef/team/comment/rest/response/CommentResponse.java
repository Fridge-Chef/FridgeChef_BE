package Fridge_Chef.team.comment.rest.response;

import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.comment.domain.CommentUserEvent;
import Fridge_Chef.team.user.domain.UserId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private String comments;
    private double star;

    private int like;
    private boolean myHit;

    private String userName;
    private List<String> imageLink;
    private Long boardId;
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(Comment comment, Optional<UserId> optional) {
        boolean myHit = false;
        if(optional.isPresent()){
            int hit =comment.getCommentUserEvent().stream()
                    .filter(v -> v.getUser().getId().equals(optional.get().getValue()))
                    .findFirst()
                    .map(CommentUserEvent::getHit)
                    .orElse(0);
            if(hit == 1){
                myHit=true;
            }
        }
        return new CommentResponse(
                comment.getId(),
                comment.getComments(),
                comment.getStar(),
                comment.getTotalHit(),
                myHit,
                comment.getUsers().getUsername(),
                comment.getImageLinks(),
                comment.getBoard().getId(),
                comment.getCreateTime()
        );
    }
}
