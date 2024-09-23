package Fridge_Chef.team.user.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.image.domain.Image;
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
    @Embedded
    private Profile profile;
    @Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Enumerated(EnumType.STRING)
    private Social social;

    public User(UserId userId, Profile profile, String email, Role role, Social social) {
        this.userId = userId;
        this.profile = profile;
        this.email = email;
        this.role = role;
        this.social = social;
        super.updateIsDelete(false);
    }

    public static User createSocialUser(String email, String name, Image picture, Role role, Social social) {
        return new User(UserId.create(), new Profile(picture, name), email, role, social);
    }

    public static User createSocialUser(String email, String name, Role role, Social social) {
        return new User(UserId.create(), new Profile(null, name), email, role, social);
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

    public String getRoleKey() {
        return this.role.value();
    }

    public User update(String name, Image picture) {
        profile.updateName(name);
        profile.updatePicture(picture);
        return this;
    }

    public void updatePicture(Image picture) {
        profile.updatePicture(picture);
    }

    public User update(String name, String picture) {
        profile.updateName(name);
        profile.updateOauthPicture(picture);
        return this;
    }

}