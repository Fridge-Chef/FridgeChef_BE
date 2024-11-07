package Fridge_Chef.team.ingredient.domain;

import java.util.Arrays;

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
    KIMCHI("김치류"),
    NONE("카테고리 없음");

    private final String value;

    IngredientCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static IngredientCategory of(String category){
        if (category == null){
            return UNCATEGORIZED;
        }
        return Arrays.stream(IngredientCategory.values())
                .filter(categorys -> categorys.getValue().equals(category))
                .findFirst()
                .orElse(UNCATEGORIZED);

    }
}
