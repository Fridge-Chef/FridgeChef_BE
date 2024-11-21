package Fridge_Chef.team.user.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.image.domain.Image;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"profile_email", "profile_social"})
})
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseEntity {
    @EmbeddedId
    private UserId userId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "email", column = @Column(name = "profile_email")),
            @AttributeOverride(name = "social", column = @Column(name = "profile_social"))
    })
    private Profile profile;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    private UserHistory history;
    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    private Fridge fridge;


    public User(UserId userId, Profile profile, Role role) {
        this.userId = userId;
        this.profile = profile;
        this.role = role;
        super.updateIsDelete(false);
    }

    public static User createSocialUser(String email, String name, Role role, Social social) {
        return new User(UserId.create(), new Profile(null, email, name, social), role);
    }


    public User(UserId userId) {
        this.userId = userId;
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

    public void updateTime() {
        history.update();
    }


    public Social getSocial() {
        return profile.getSocial();
    }

    public String getEmail() {
        return profile.getEmail();
    }

    public void updateUsername(String username) {
        this.profile.updateName(username);
    }


    public String getImageLink() {
        if (profile != null && profile.getPicture() != null) {
            return profile.getPicture().getLink();
        }
        return "";
    }
}