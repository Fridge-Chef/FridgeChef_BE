package Fridge_Chef.team.cert.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.common.entity.OracleBoolean;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Cert extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private int verificationCode;
    private String email;
    @Enumerated(value = EnumType.STRING)
    private OracleBoolean authentication;

    public Cert(int verificationCode, String email, OracleBoolean authentication) {
        this.verificationCode = verificationCode;
        this.email = email;
        this.authentication=authentication;
    }

    public void updateAuthentication(boolean authentication){
        this.authentication=OracleBoolean.of(authentication);
    }
}
