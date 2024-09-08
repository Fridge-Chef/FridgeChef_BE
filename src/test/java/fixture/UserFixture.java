package fixture;

import Fridge_Chef.team.user.domain.User;

public class UserFixture {
    public static User create(String email, String password) {
        return User.create(email, password, "user_name_test_1");
    }
}
