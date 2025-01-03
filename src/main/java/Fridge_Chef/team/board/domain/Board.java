package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_seq_gen")
    @SequenceGenerator(name = "board_seq_gen", sequenceName = "board_seq", allocationSize = 1)
    private Long id;
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, orphanRemoval = true,cascade = CascadeType.PERSIST)
    private List<BoardUserEvent> boardUserEvent = new ArrayList<>();
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, orphanRemoval = true,cascade = CascadeType.PERSIST)
    private List<BoardIssue> boardIssues= new ArrayList<>();
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, orphanRemoval = true,cascade = CascadeType.PERSIST)
    private List<BoardHistory> historys= new ArrayList<>();
    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, orphanRemoval = true,cascade = CascadeType.PERSIST)
    private List<Comment> comments= new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String title;
    private String introduction;
    @Embedded
    private Context context;
    @Enumerated(EnumType.STRING)
    private BoardType type;
    @ManyToOne(fetch = FetchType.LAZY)
    private Image mainImage;
    private String pathMainImage;
    private double totalStar;
    private int hit;
    private int count;

    public Board(User user, String introduction, String title, Context context, Image mainImage, BoardType type) {
        this.user = user;
        this.introduction=introduction;
        this.title = title;
        this.context = context;
        this.mainImage = mainImage;
        this.type = type;
        this.totalStar = 0L;
        this.hit = 0;
        this.count = 0;

        if(mainImage != null){
            pathMainImage = mainImage.getLink();
        }
    }

    public static Board from(User user,String intro,String name, Context context,Image mainImage) {
        return new Board(user,intro, name, context, mainImage, BoardType.OPEN_API);
    }

    public static Board from(User user,String intro,String name,Image mainImage,BoardType boardType){
        return new Board(user,intro, name, null, mainImage, boardType);
    }

    public static Board from(User user,String intro,String name, Context context,Image mainImage,BoardType boardType) {
        return new Board(user,intro, name, context, mainImage,boardType);
    }

    public void updateStar(double totalStar) {
        this.totalStar = totalStar;
    }

    public void updateHit(int hit) {
        this.hit = hit;
    }

    public void updateCount() {
        this.count++;
    }

    public void updateCount(int count) {
        this.count = count;
    }

    public boolean isMainImageEmpty() {
        return mainImage == null;
    }

    public void updateMainImage(Image mainImage) {
        this.mainImage = mainImage;
    }

    public String getMainImageLink() {
        if (mainImage != null) {
            return mainImage.getLink();
        }
        return "";
    }

    public void addUserEvent(BoardUserEvent boardUserEvent) {
        this.boardUserEvent.add(boardUserEvent);
    }

    public void updateId(Long id) {
        this.id = id;
    }

    public boolean getIsMyHit(UserId userId) {
        if(userId == null){
            return false;
        }
        return boardUserEvent.stream()
                .anyMatch(event -> event.isUserHit(userId));
    }

    public Long getMainImageId() {
        if (mainImage != null) {
            return mainImage.getId();
        }
        return -1L;
    }

    public void updateContext(List<RecipeIngredient> ingredients, List<Description> descriptions, String dishTime, String dishLevel, String dishCategory){
        if(context == null){
            this.context = Context.formMyUserRecipe(dishTime,dishLevel,dishCategory,ingredients, descriptions);
        }
        context.update(ingredients, descriptions,dishTime,dishLevel,dishCategory);
    }

    public void updateContext(List<Description> descriptions, String introduction,String title,String dishTime, String dishLevel, String dishCategory){
        this.title=title;
        this.introduction=introduction;
        context.update(descriptions,dishTime,dishLevel,dishCategory);
    }


    public int hitTotalCount() {
        return boardUserEvent.stream()
                .mapToInt(BoardUserEvent::getHit)
                .sum();
    }

    public int starTotalCount() {
        return comments.size();
    }

    public void addHistory(BoardHistory boardHistory){
        this.historys.add(boardHistory);
    }

    public void commentClear() {
        this.comments.clear();
    }

    public void updateComment(List<Comment> comments) {
        this.comments.addAll(comments);
    }

    public void addIssue(BoardIssue issue) {
        this.boardIssues.add(issue);
    }
}
