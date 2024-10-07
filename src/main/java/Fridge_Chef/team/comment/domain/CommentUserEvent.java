package Fridge_Chef.team.comment.domain;

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
    @JoinColumn
    private Comment board;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private int hit;

    public CommentUserEvent(Comment board, User user, int hit) {
        this.board = board;
        this.user = user;
        this.hit = hit;
    }
}
