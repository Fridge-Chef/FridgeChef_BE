package Fridge_Chef.team.comment.domain;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class CommentUserEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment comments;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private int hit;

    public CommentUserEvent(Board board, Comment comments, User user) {
        this.board = board;
        this.comments = comments;
        this.user = user;
        this.hit = 0;
    }

    public void updateHit() {
        if (hit == 0) {
            hit = 1;
        } else {
            hit = 0;
        }
    }
}
