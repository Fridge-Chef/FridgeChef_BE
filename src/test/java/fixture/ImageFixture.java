package fixture;

import Fridge_Chef.team.common.docs.CustomMockPartFile;
import Fridge_Chef.team.image.domain.Image;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

public class ImageFixture {
    public static Image create() {
        return Image.outUri("test.png");
    }

    public static Image create(Long id) {
        return Image.outUri(id,"test.png");
    }

    public static MockMultipartFile partMockImage(String name) {
        try {
            return new MockMultipartFile(
                    name,
                    "test.png",
                    MediaType.IMAGE_PNG_VALUE,
                    new ClassPathResource("test.png").getInputStream()
            );
        }catch (Exception e){
            return new MockMultipartFile(name,"test.png",  MediaType.IMAGE_PNG_VALUE,"".getBytes());
        }
    }

    public static CustomMockPartFile partImage(String name) {
        try {
            return new CustomMockPartFile(
                    name,
                    "test.png",
                    MediaType.IMAGE_PNG_VALUE,
                    new ClassPathResource("test.png").getInputStream().readAllBytes()
            );
        }catch (Exception e){
            return new CustomMockPartFile(name,"test.png",  MediaType.IMAGE_PNG_VALUE,"".getBytes());
        }
    }
    public static CustomMockPartFile partImage(String name,String description){
        try {
            return new CustomMockPartFile(
                    name,
                    "test.png",
                    MediaType.IMAGE_PNG_VALUE,
                    new ClassPathResource("test.png").getInputStream().readAllBytes(),
                    description,
                    true
            );
        }catch (Exception e){
            return new CustomMockPartFile(name,"test.png",  MediaType.IMAGE_PNG_VALUE,"".getBytes());
        }
    }
    public static CustomMockPartFile partImage(String name,String description,boolean option){
        try {
            return new CustomMockPartFile(
                    name,
                    "test.png",
                    MediaType.IMAGE_PNG_VALUE,
                    new ClassPathResource("test.png").getInputStream().readAllBytes(),
                    description,
                    option
            );
        }catch (Exception e){
            return new CustomMockPartFile(name,"test.png",  MediaType.IMAGE_PNG_VALUE,"".getBytes());
        }
    }

    public static MockMultipartFile partMockImage(String name, String value) {
        return new MockMultipartFile(
                name,
                value.getBytes()
        );
    }
}
