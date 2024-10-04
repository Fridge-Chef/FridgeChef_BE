package Fridge_Chef.team.board.repository.model;

public enum SortType {
    LATEST("최신순"),
    RATING("별점순"),
    CLICKS("클릭순"),
    HIT("히트순");

    private final String description;

    SortType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}