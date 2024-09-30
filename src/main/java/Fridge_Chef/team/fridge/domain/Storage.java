package Fridge_Chef.team.fridge.domain;

public enum Storage {

    REFRIGERATION("냉장 보관"),
    TEMPERATURE("실온 보관");

    private final String value;

    Storage(String value) {
        this.value = value;
    }
}
