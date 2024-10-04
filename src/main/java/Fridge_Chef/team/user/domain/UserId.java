package Fridge_Chef.team.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;


@Embeddable
public class UserId implements Serializable {

    @Column(name = "id", updatable = false, nullable = false)
    private final UUID value;

    protected UserId() {
        this.value = UUID.randomUUID();
    }

    public UserId(UUID value) {
        this.value = value;
    }

    public UserId(String value) {
        this.value = UUID.fromString(Objects.requireNonNull(value, "String value must not be null"));
    }

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return value.equals(userId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}