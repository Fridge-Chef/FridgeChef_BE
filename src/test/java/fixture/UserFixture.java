package fixture;

import Fridge_Chef.team.user.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static Fridge_Chef.team.user.domain.Social.GOOGLE;
import static Fridge_Chef.team.user.domain.Social.KAKAO;

public class UserFixture {
    private static Random random = new Random();
    public static User create(String email) {
        String uuid =  UUID.randomUUID().toString();
        return User.createSocialUser(uuid+email, uuid, Role.USER, KAKAO);
    }

    public static User createId(String email) {
        String uuid =  UUID.randomUUID().toString();
        return new User(UserId.create(),new Profile(null,email, uuid, KAKAO),Role.USER);
    }

    public static List<User> creates(int size) {
        List<User> list = new ArrayList<>();
        for(int i=0;i<size;i++){
            String uuid =  UUID.randomUUID().toString();
            Social social= random.nextBoolean() ? GOOGLE : KAKAO;
            User user = User.createSocialUser(uuid+"test@gmail.com", uuid, Role.USER, social);
            list.add(user);
        }
        return list;
    }
}
