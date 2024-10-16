package Fridge_Chef.team.ingredient.domain;

public enum IngredientCategory {

    UNCATEGORIZED("미분류"),
    VEGETABLES("채소류"),
    MEATS("육류"),
    FISHES("생선류"),
    SEA_FOODS("해산물류"),
    FRUITS("과일류"),
    GRAINS("곡류"),
    LEGUMES("콩류"),
    EGGS("달걀류"),
    DAIRY("유제품"),
    BREADS("빵류"),
    PROCESSED_FOODS("가공식품"),
    KIMCHI("김치류");

    private final String value;

    IngredientCategory(String value) {
        this.value = value;
    }
}
