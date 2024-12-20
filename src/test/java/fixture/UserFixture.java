package fixture;

import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static Fridge_Chef.team.user.domain.Social.GOOGLE;
import static Fridge_Chef.team.user.domain.Social.KAKAO;

public class UserFixture {
    private static final Random random = new Random();

    public static User create(String email) {
        String uuid =  UUID.randomUUID().toString();
        User user = User.createSocialUser(uuid+email, uuid, Role.USER, KAKAO);
        user.updatePicture(Image.outUri("https://oracle.cloud.com/0/dmiewndx/p/sample.jpg"));
        return user;
    }

    public static List<User> creates(int size) {
        List<User> list = new ArrayList<>();
        for(int i=0;i<size;i++){
            String uuid =  UUID.randomUUID().toString();
            Social social= random.nextBoolean() ? GOOGLE : KAKAO;
            User user = User.createSocialUser(uuid+"test@gmail.com", uuid, Role.USER, social);
            user.updatePicture(Image.outUri("https://oracle.cloud.com/0/dmiewndx/p/sample.jpg"));
            list.add(user);
        }
        return list;
    }
}
