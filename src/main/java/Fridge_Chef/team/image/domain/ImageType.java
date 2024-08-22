package Fridge_Chef.team.image.domain;

public enum ImageType {
    ORACLE_CLOUD("오라클 클라우드"),
    DATA_GO("정부 공공 데이터");
    private final String value;

    ImageType(String value) {
        this.value = value;
    }
}
