package Fridge_Chef.team.user.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseEntity {
    @EmbeddedId
    private UserId userId;
    @Column(unique = true)
    private String email;
    private String password;
    @Embedded
    private Profile profile;
    @Enumerated(EnumType.STRING)
    private Role role;

    private User(UserId userId, String email, String password, Profile profile, Role role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.profile = profile;
        this.role = role;
        super.updateIsDelete(false);
    }

    public static User create(String email, String password, String nickname, Role role) {
        return new User(UserId.create(), email, password,
                new Profile(nickname), role);
    }

    public static User create(String email, String password, String nickname) {
        return new User(UserId.create(), email, password,
                new Profile(nickname), Role.USER);
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void accountDelete(boolean isDelete) {
        updateIsDelete(isDelete);
    }

    public String getUsername() {
        return profile.getUsername();
    }

    public UUID getId() {
        return this.userId.getValue();
    }
}