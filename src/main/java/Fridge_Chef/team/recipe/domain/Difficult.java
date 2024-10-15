package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;

public enum Difficult {

    EASY("쉬움"),
    NORMAL("중간"),
    HARD("어려움");

    private final String value;

    Difficult(String value) {
        this.value = value;
    }

    public static Difficult of(String value) {
        for (Difficult difficult : values()) {
            if (difficult.value.equals(value)) {
                return difficult;
            }
        }

        throw new ApiException(ErrorCode.RECIPE_DIFFICULT_INVALID);
    }

    public String getValue() {
        return value;
    }
}
