package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class BoardUserEvent extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private int hit;
    private double star;

    public BoardUserEvent(Board board,User user) {
        this.board =board;
        this.user = user;
        this.hit = 0;
        this.star =0;
    }

    public void hitUp(){
        if(hit == 1){
            hit =0;
        }else{
            hit = 1;
        }
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
}
