package Fridge_Chef.team.board.domain;


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
public class BoardHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    private int count;

    public BoardHistory(Board board, int count) {
        this.board = board;
        this.count = count;
    }

    public void countUp() {
        this.count++;
    }
}
