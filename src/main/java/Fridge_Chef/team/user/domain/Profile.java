package Fridge_Chef.team.user.domain;

import Fridge_Chef.team.image.domain.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Image picture;

    @Column
    private String email;
    @Column
    private String username;

    @Enumerated(EnumType.STRING)
    private Social social;

    protected void updateName(String name){
        this.username=name;
    }

    public void updatePicture(Image picture) {
        this.picture=picture;
    }

    public void updateOauthPicture(String picture) {
        this.picture.updateOutUri(picture);
    }
}
