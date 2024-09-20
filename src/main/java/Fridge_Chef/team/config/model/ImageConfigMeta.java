package Fridge_Chef.team.config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageConfigMeta {
    String namespaceName;
    String bucketName;
    String url;
    String uploadPath;
    String removePath;
}
