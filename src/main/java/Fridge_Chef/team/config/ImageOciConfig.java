package Fridge_Chef.team.config;

import Fridge_Chef.team.config.model.ImageConfigMeta;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ImageOciConfig {
    private String configurationFilePath = "src/main/resources/config";
    private UploadConfiguration uploadConfiguration;
    @Value("${oci.namespace.name}")
    private String namespaceName;
    @Value("${oci.bucket.name}")
    private String bucketName;
    @Value("${oci.url.path}")
    private String url;
    @Value("${oci.upload.path}")
    private String uploadPath;
    @Value("${oci.remove.path}")
    private String removePath;


    @Bean
    ObjectStorage ociObjectStorage() {
        try {
            return ObjectStorageClient.builder()
                    .region(Region.AP_CHUNCHEON_1)
                    .build(new ConfigFileAuthenticationDetailsProvider(
                            ConfigFileReader.parse(configurationFilePath, "DEFAULT"))
                    );
        } catch (IOException e) {
            throw new ApiException(ErrorCode.IMAGE_REMOTE_SESSION);
        }
    }

    @Bean
    UploadManager uploadManager() {
        uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();
        return new UploadManager(ociObjectStorage(), uploadConfiguration);
    }

    @Bean
    ImageConfigMeta imageConfigRequest() {
        return new ImageConfigMeta(namespaceName, bucketName, url, uploadPath, removePath);
    }
}
