package Fridge_Chef.team.board.repository.model;

import lombok.Getter;

@Getter
public enum BookType {
    HIT("찜하기"),
    MYRECIPE("나만의 레시피"),
    COMMENT("레시피 후기");
    private final String name;

    BookType(String name) {
        this.name = name;
    }
}
