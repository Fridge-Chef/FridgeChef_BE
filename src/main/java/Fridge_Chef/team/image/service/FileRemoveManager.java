package Fridge_Chef.team.image.service;

import Fridge_Chef.team.config.model.ImageConfigMeta;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;

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
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucketName(meta.getBucketName())
                .namespaceName(meta.getNamespaceName())
                .objectName(image.getName())
                .build();
        try {

            storageClient.deleteObject(request);
        } catch (BmcException e) {
            throw new ApiException(ErrorCode.IMAGE_FILE_DELETE_FAIL);
        }
    }
}