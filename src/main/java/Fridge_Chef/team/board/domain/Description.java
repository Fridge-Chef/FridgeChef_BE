package Fridge_Chef.team.board.domain;


import Fridge_Chef.team.image.domain.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Description {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    public Description(String description, Image image) {
        this.description = description;
        this.image = image;
    }

    public Description(Long id, Board board, String description, Image image) {
        this.id = id;
        this.board = board;
        this.description = description;
        this.image = image;
    }

    public Description update(String description, Image image) {
        this.description = description;
        this.image = image;
        return this;
    }
    public void update(String description){
        this.description=description;
    }
    public  void update(Image image){
        this.image=image;
    }

    public String getLink() {
        if (image != null) {
            return image.getLink();
        }
        return "";
    }
}