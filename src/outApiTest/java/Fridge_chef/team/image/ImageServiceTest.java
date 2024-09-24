package Fridge_chef.team.image;

import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageProdService;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.Social;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_chef.team.FridgeChefApplicationApiTest;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("이미지")
public class ImageServiceTest extends FridgeChefApplicationApiTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageProdService imageService;

    @Value("${oci.namespace.name}")
    private String namespaceName;
    @Value("${oci.bucket.name}")
    private String bucketName;
    @Value("${oci.upload.path}")
    private String upload;
    @Value("${oci.url.path}")
    private String url;
    private Path path = Paths.get("src/outApiTest/resources/fridge_chef.png");
    private File file = path.toFile();


    @Test
    @DisplayName("서비스에서 이미지 추가")
    void upload() throws IOException {
        String name = "test_"+file.getName();
        MultipartFile body = new MockMultipartFile(
                name,
                name,
                "image/png",
                Files.readAllBytes(file.toPath())
        );
        String path = url + upload + name;

        imageService.upload(body, body.getName());

        assertThat(isUrlConnect(path)).isTrue();
    }

    @Test
    @DisplayName("서비스 삭제")
    void remove() throws IOException {
        File file = path.toFile();
        MultipartFile body = new MockMultipartFile(
                file.getName(),
                file.getName(),
                "image/png",
                Files.readAllBytes(file.toPath())
        );

        User user = userRepository.save(User.createSocialUser("test2@gmail.com", "test2", Role.USER, Social.KAKAO));
        Image image = imageService.imageUpload(user.getUserId(), body);
        String path = url + upload + image.getName();

        assertThat(isUrlConnect(path)).isTrue();

        imageService.imageRemove(user.getUserId(), image.getId());
    }

    @Test
    @DisplayName("네이티브 연결-파일 전송")
    void connect() throws IOException {
        String configurationFilePath = "src/main/resources/private/config";
        String profile = "DEFAULT";
        String objectName = "fridge_chef.png";
        String contentType = "image/png";
        File body = path.toFile();

        ObjectStorage client = ObjectStorageClient.builder()
                .region(Region.AP_CHUNCHEON_1)
                .build(new ConfigFileAuthenticationDetailsProvider(
                        ConfigFileReader.parse(configurationFilePath, profile))
                );

        UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();

        UploadManager uploadManager = new UploadManager(client, uploadConfiguration);

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(bucketName)
                        .namespaceName(namespaceName)
                        .objectName(objectName)
                        .contentType(contentType)
                        .contentLanguage(null)
                        .contentEncoding(null)
                        .opcMeta(null)
                        .build();

        UploadManager.UploadRequest uploadDetails =
                UploadManager.UploadRequest.builder(body).allowOverwrite(true).build(request);

        uploadManager.upload(uploadDetails);
        client.getObject(
                GetObjectRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .objectName(objectName)
                        .build());
    }

}
