package Fridge_Chef.team.image.service;

import Fridge_Chef.team.config.model.ImageConfigMeta;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileRemoveManager {

    private final ImageConfigMeta meta;
    private final ObjectStorage storageClient;
    private final Image image;

    public FileRemoveManager(ObjectStorage storageClient, ImageConfigMeta meta, Image image) {
        this.storageClient = storageClient;
        this.meta = meta;
        this.image = image;
    }

    public void remove() {
        log.info("이미지 제거 이름 " + image.getName() +" , link :"+ image.getLink() +"");
        log.info("이미지 path :" + image.getPath());
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucketName(meta.getBucketName())
                .namespaceName(meta.getNamespaceName())
                .objectName(image.getName())
                .build();
        try {
            storageClient.deleteObject(request);
        } catch (BmcException e) {
            if(e.getStatusCode() == 404 ){
                return;
            }
            throw new ApiException(ErrorCode.IMAGE_FILE_DELETE_FAIL);
        }
    }
}