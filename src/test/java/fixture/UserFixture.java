package fixture;

import Fridge_Chef.team.user.domain.*;

import java.util.UUID;

public class UserFixture {
    public static User create(String email) {
        String uuid =  UUID.randomUUID().toString();
        return User.createSocialUser(uuid+email, uuid, Role.USER,Social.KAKAO);
    }

    public static User createId(String email) {
        String uuid =  UUID.randomUUID().toString();
        return new User(UserId.create(),new Profile(null,uuid),uuid+email,Role.USER,Social.KAKAO);
    }
}
