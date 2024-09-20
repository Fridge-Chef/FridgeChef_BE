package Fridge_Chef.team.image.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.user.domain.UserId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "images")
@NoArgsConstructor(access = PROTECTED)
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String uri;
    private String path;
    private String name;
    @Enumerated
    private ImageType type;
    @Column(name = "user_id")
    private UUID userId;


    public Image(String uri, String path, String name, ImageType type, UserId userId) {
        this.uri = uri;
        this.path = path;
        this.name = name;
        this.type = type;
        this.userId = userId.getValue();
    }

    public Image(String path, ImageType type) {
        this.path = path;
        String[] parts = path.split("/");
        this.name = parts[parts.length - 1];
        this.type = type;
    }

    public Image(String uri, String path, ImageType type) {
        this.uri = uri;
        this.path = path;
        this.type = type;
    }

    public static Image outUri(String imageUrl) {
        return new Image(imageUrl, ImageType.DATA_GO);
    }
}
