package Fridge_Chef.team.image.service;

import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@Service
@Profile("local")
@RequiredArgsConstructor
public class ImageLocalService implements ImageService {

    @Override
    public Image imageUpload(UserId userId, MultipartFile file) {
        return null;
    }

    @Override
    public Image openApiUriImageSave(String path) {
        return null;
    }

    @Override
    public void imageRemove(UserId userId, Long imageId) {

    }

    @Override
    public void imageRemove(Long imageId) {

    }

    @Override
    public void upload(MultipartFile multipartFile, String fileName) {

    }

    @Override
    public void filter(MultipartFile file) {

    }

    @Override
    public void filters(List<MultipartFile> files) {

    }

    @Override
    public List<Image> imageUploads(UserId userId, List<MultipartFile> files) {
        return null;
    }

    private String onlyNameChange(String name) {
        String uuidString = UUID.randomUUID().toString();
        return uuidString + "_" + name;
    }
}
