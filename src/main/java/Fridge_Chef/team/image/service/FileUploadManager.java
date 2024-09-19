package Fridge_Chef.team.image.service;

import Fridge_Chef.team.config.model.ImageConfigMeta;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUploadManager {

    private final ImageConfigMeta imageConfigMeta;
    private final UploadManager uploadManager;
    private final UploadManager.UploadRequest request;

    public FileUploadManager(UploadManager uploadManager, ImageConfigMeta imageConfigMeta, MultipartFile file, String fileName) {
        this.uploadManager = uploadManager;
        this.request = fileReadWith(fileName, file.getContentType(), multiToFile(file));
        this.imageConfigMeta = imageConfigMeta;
    }

    public void upload() {
        try {
            uploadManager.upload(request);
        } catch (BmcException e) {
            throw new ApiException(ErrorCode.IMAGE_REMOTE_UPLOAD);
        }
    }

    private File multiToFile(MultipartFile file) {
        try {
            return file.getResource().getFile();
        } catch (IOException e) {
            throw new ApiException(ErrorCode.IMAGE_FILE_ANALYIS);
        }
    }

    private UploadManager.UploadRequest fileReadWith(String objectName, String contentType, File file) {
        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(imageConfigMeta.getBucketName())
                        .namespaceName(imageConfigMeta.getNamespaceName())
                        .objectName(objectName)
                        .contentType(contentType)
                        .build();

        return UploadManager.UploadRequest.builder(file)
                .allowOverwrite(true)
                .build(request);
    }
}