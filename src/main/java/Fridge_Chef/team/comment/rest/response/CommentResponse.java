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
    private String title;
    private String comments;
    private double star;

    private int like;
    private boolean myHit;

    private String userName;
    private boolean myMe;
    private List<String> imageLink;
    private Long boardId;
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(Comment comment, Optional<UserId> optional) {
        boolean myHit = false;
        boolean myMe =false;

        if (optional.isPresent()) {
            if(comment.getUsers().getUserId().equals(optional.get())){
                myMe=true;
            }
            int hit = comment.getCommentUserEvent().stream()
                    .filter(v -> v.getUser().getId().equals(optional.get().getValue()))
                    .findFirst()
                    .map(CommentUserEvent::getHit)
                    .orElse(0);
            if (hit == 1) {
                myHit = true;
            }
        }
        return new CommentResponse(
                comment.getId(),
                comment.getBoard().getTitle(),
                comment.getComments(),
                comment.getStar(),
                comment.getTotalHit(),
                myHit,
                comment.getUsers().getUsername(),
                myMe,
                comment.getImageLinks(),
                comment.getBoard().getId(),
                comment.getCreateTime()
        );
    }

    public static CommentResponse fromMyEntity(Comment comment, UserId userId) {
        boolean isMyHit = comment.getCommentUserEvent().stream()
                .filter(v -> v.getUser().getId().equals(userId.getValue()))
                .findFirst()
                .map(CommentUserEvent::getHit)
                .orElse(0) == 1;

        return new CommentResponse(
                comment.getId(),
                comment.getBoard().getTitle(),
                comment.getComments(),
                comment.getStar(),
                comment.getTotalHit(),
                isMyHit,
                comment.getUsers().getUsername(),
                comment.getUsers().getUserId().equals(userId),
                comment.getImageLinks(),
                comment.getBoard().getId(),
                comment.getCreateTime()
        );
    }
}
