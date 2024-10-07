package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class BoardIssue extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne( fetch = FetchType.LAZY)
    private Board board;

    public BoardIssue(Board board) {
        this.board = board;
    }
}
