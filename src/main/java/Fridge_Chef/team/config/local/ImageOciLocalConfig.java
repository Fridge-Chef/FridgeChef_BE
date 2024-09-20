package Fridge_Chef.team.config.local;

import Fridge_Chef.team.config.model.ImageConfigMeta;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile("local")
@Configuration
public class ImageOciLocalConfig {

    @Bean
    ObjectStorage ociObjectStorage() {
        return null;
    }

    @Bean
    UploadManager uploadManager() {
        return null;
    }

    @Bean
    ImageConfigMeta imageConfigRequest() {
        return null;
    }
}
