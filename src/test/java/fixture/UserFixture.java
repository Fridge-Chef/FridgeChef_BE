package fixture;

import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.Social;
import Fridge_Chef.team.user.domain.User;

public class UserFixture {
    public static User create(String email) {
        return User.createSocialUser(email, "user_name_test_1", Role.USER,Social.KAKAO);
    }
}
