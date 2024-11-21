package Fridge_Chef.team.image.service;

import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    Image uploadImageWithId(UserId userId, boolean isImage,Long imageId, MultipartFile file);
    Image imageUpload(UserId userId, MultipartFile file);
    Image imageUploadUserPicture(User user, MultipartFile file);

    Image openApiUriImageSave(String path);

    void imageRemove(UserId userId, Long imageId);

    void imageRemove(Long imageId);

    void upload(MultipartFile multipartFile, String fileName);

    void filter(MultipartFile file);

    void filters(List<MultipartFile> files);

    List<Image> imageUploads(UserId userId, List<MultipartFile> files);

    void imageRemove(Image image);

}
