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

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "comments")
@NoArgsConstructor(access = PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CommentUserEvent> commentUserEvent;
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.PERSIST )
    private List<Image> commentImage;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    private User users;

    private String comments;
    private double star;
    private int totalHit;

    public Comment(Board board, User users, List<Image> images, String comments, double star) {
        this.board = board;
        this.users = users;
        this.comments = comments;
        this.star = star;
        this.totalHit=0;
        this.commentImage = images;
        this.commentUserEvent = new ArrayList<>();
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

    public List<String> getImageLink(){
        List<String> links = new ArrayList<>();
        if(commentImage != null){
            for(var image : commentImage){
                links.add(image.getLink());
            }
        }
        return links;
    }
    public Comment updateId(Long id){
        this.id=id;
        return this;
    }

    public void updateImage(List<Image> image) {
        this.commentImage=image;
    }
    public void updateHit(int totalHit) {
        this.totalHit = totalHit;
    }

    public void updateComment(String comment) {
        this.comments =comment;
    }

    public List<String> getImageLinks() {
        List<String> list = new ArrayList<>();
        if(commentImage==null){
            return list;
        }
        commentImage.forEach(comment -> {
            System.out.println("response get img link :"+comment.getLink());
                list.add(comment.getLink());
        });
        return list;
    }

    public void updateComments(List<Image> images) {
        this.commentImage=images;
    }

    public void addUserEvent(CommentUserEvent commentUserEvent) {
        this.commentUserEvent.add(commentUserEvent);
    }

    public void removeImage() {
        this.commentImage.clear();
    }
}
