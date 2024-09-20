package Fridge_Chef.team.config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageConfigMeta {
    private String namespaceName;
    private String bucketName;
    private String url;
    private String uploadPath;
    private String removePath;
}
