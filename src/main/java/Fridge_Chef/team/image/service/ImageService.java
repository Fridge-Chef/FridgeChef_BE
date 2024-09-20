package Fridge_Chef.team.image.service;

import Fridge_Chef.team.config.model.ImageConfigMeta;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.domain.ImageType;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.user.domain.UserId;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final static Pattern mimePattern = Pattern.compile("image/(png|jpeg|jpg)");
    private final UploadManager uploadManager;
    private final ObjectStorageClient objectStorageClient;
    private final ImageRepository imageRepository;
    private final ImageConfigMeta imageConfigMeta;


    @Transactional
    public Image imageUpload(UserId userId, MultipartFile file) {
        String fileName = onlyNameChange(file.getName());
        upload(file, fileName);
        Image image = new Image(imageConfigMeta.getUrl(), imageConfigMeta.getUploadPath(), fileName, ImageType.ORACLE_CLOUD, userId);
        return imageRepository.save(image);
    }

    @Transactional
    public Image openApiUriImageSave(String path) {
        return imageRepository.save(new Image(path, ImageType.DATA_GO));
    }

    @Transactional
    public void imageRemove(UserId userId, Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(ErrorCode.IMAGE_NOT_ID));

        if (image.getUserId() != null && !image.getUserId().equals(userId.getValue())) {
            throw new ApiException(ErrorCode.IMAGE_AUTHOR_MISMATCH);
        }

        FileRemoveManager manager = new FileRemoveManager(objectStorageClient, imageConfigMeta, image);
        manager.remove();
    }

    @Transactional
    public void imageRemove(Long imageId) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ApiException(ErrorCode.IMAGE_NOT_ID));

        FileRemoveManager file = new FileRemoveManager(objectStorageClient, imageConfigMeta, image);
        file.remove();
    }

    public void upload(MultipartFile multipartFile, String fileName) {
        FileUploadManager file = new FileUploadManager(uploadManager, imageConfigMeta, multipartFile, fileName);
        file.upload();
    }

    public void filter(MultipartFile file) {
        String type = file.getContentType();
        if (type == null || !mimePattern.matcher(type).matches()) {
            throw new ApiException(ErrorCode.IMAGE_CONTENT_TYPE_FAIL);
        }
    }

    public void filters(List<MultipartFile> files) {
        files.forEach(this::filter);
    }

    public List<Image> imageUploads(UserId userId, List<MultipartFile> files) {
        filters(files);
        return files.stream()
                .map(file -> imageUpload(userId, file))
                .collect(Collectors.toList());
    }

    private String onlyNameChange(String name) {
        String uuidString = UUID.randomUUID().toString();
        return uuidString + "_" + name;
    }
}
