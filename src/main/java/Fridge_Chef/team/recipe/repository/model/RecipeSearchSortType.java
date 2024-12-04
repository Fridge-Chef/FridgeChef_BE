package Fridge_Chef.team.recipe.repository.model;

public enum RecipeSearchSortType {

    LATEST("최신순"),
    RATING("별점순"),
    MATCH("매칭순"),
    HIT("좋아요순");

    private final String value;

    RecipeSearchSortType(String description) {
        this.value = description;
    }

    public String getValue() {
        return value;
    }
}
