package Fridge_Chef.team.image.domain;

public enum ImageType {
    ORACLE_CLOUD("오라클 클라우드"),
    OUT_URI("외부 링크");
    private final String value;

    ImageType(String value) {
        this.value = value;
    }
}
