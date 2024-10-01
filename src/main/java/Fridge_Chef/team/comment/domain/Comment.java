package Fridge_Chef.team.comment.domain;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    @OneToOne(fetch = FetchType.LAZY)
    private Image commentImage;
    private String comment;
    private double star;


    public Comment(Board board, User user, Image commentImage, String comment, double star) {
        this.board = board;
        this.user = user;
        this.commentImage = commentImage;
        this.comment = comment;
        this.star = star;
    }


    public void updateStar(double star) {
        if (star < 1.0 || star > 5.0) {
            throw new ApiException(ErrorCode.STAR_RATING_IS_1_0_OR_HIGHER_AND_5_0_OR_LOWER);
        }

        if (star * 2 != Math.floor(star * 2)) {
            throw new ApiException(ErrorCode.RATING_IS_0_5_UNITS);
        }
        this.star = star;
    }

    public String getImageLink(){
        if(commentImage != null){
            return commentImage.getLink();
        }
        return "";
    }
}
