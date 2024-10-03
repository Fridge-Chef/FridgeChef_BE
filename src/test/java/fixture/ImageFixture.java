package fixture;

import Fridge_Chef.team.image.domain.Image;

public class ImageFixture {
    public static Image create() {
        return Image.outUri("test.png");
    }
}
