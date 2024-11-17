package fixture;

import Fridge_Chef.team.image.domain.Image;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

public class ImageFixture {
    public static Image create() {
        return Image.outUri("test.png");
    }

    public static Image create(Long id) {
        return Image.outUri(id,"test.png");
    }

    public static MockMultipartFile getMultiFile(String name) throws IOException {
        return new MockMultipartFile(
                name,
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                new ClassPathResource("test.png").getInputStream()
        );
    }

    public static MockMultipartFile getMultiFile(String name, String value) {
        return new MockMultipartFile(
                name,
                value.getBytes()
        );
    }
}
