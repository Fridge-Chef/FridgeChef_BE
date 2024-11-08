package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;

public enum Difficult {
    EASY("쉬움"),
    NORMAL_1("중간"),
    NORMAL_2("보통"),
    HARD("어려움"),
    NONE("");

    private final String value;

    Difficult(String value) {
        this.value = value;
    }

    public static Difficult of(String value) {
        if(value == null){
            return NONE;
        }
        for (Difficult difficult : values()) {
            if (difficult.value.equals(value)) {
                return difficult;
            }
        }
        try {
            return Difficult.valueOf(value);
        }catch (IllegalArgumentException ignored){}
        throw new ApiException(ErrorCode.RECIPE_DIFFICULT_INVALID);
    }

    public String getValue() {
        return value;
    }
}
