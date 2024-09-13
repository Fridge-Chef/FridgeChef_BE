package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @Column(name = "recipe_id", columnDefinition = "BINARY(16)")
    private UUID id;
    private String name;

    @Column(name = "instructions", columnDefinition = "TEXT")   //임시로 길이 늘림. 수정 필요
    private String instructions;
    private String imageUrl;
    private List<String> ingredients;
}