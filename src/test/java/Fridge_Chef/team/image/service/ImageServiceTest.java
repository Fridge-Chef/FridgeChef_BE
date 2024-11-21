package Fridge_Chef.team.image.service;

import Fridge_Chef.team.common.ServiceLayerTest;
import Fridge_Chef.team.user.domain.UserId;
import fixture.UserFixture;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

// TODO : 실제 이미지는 수동 테스트 에서 진행됩니다.
// src/externalApiTest/image
public class ImageServiceTest extends ServiceLayerTest {
    @InjectMocks
    private ImageLocalService imageService;

    @Test
    void fakeImageUpload() {
        imageService.upload(null, "filename");
        imageService.uploadImageWithId(UserId.create(), false, 1L, null);
        imageService.imageUpload(UserId.create(), null);
        imageService.imageUpload(UserId.create(), null);
        imageService.imageUploadUserPicture(UserFixture.create("test@test.com"), null);
    }

    @Test
    void fakeImageRemove() {
        imageService.imageRemove(UserId.create(), 1L);
        imageService.imageRemove(1L);
    }
}
