package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.user.domain.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Board board;
    private UUID user_id;

    public Post(Board board, UserId user) {
        this.board = board;
        this.user_id = user.getValue();
    }
}
