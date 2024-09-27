package Fridge_Chef.team.user.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class UserHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @ElementCollection
    private List<LocalDateTime> time = new ArrayList<>();

    public UserHistory(User userId) {
        this.userId = userId;
    }

    public void update() {
        time.add(LocalDateTime.now());
    }
}
