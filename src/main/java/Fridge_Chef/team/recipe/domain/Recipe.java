package Fridge_Chef.team.recipe.domain;


import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Builder
@Entity
@Getter
@Table
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Recipe  extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @Column(name = "recipe_id", columnDefinition = "BINARY(16)")
    private UUID id;
    private String name; // 레시피 이름
    private String category; // 레시피 카테고리 (한식, 양식 등)

    @Column(name = "instructions", columnDefinition = "TEXT")   //임시로 길이 늘림
    private String instructions; // 요리 순서
    private String imageUrl; // 레시피 이미지 URL
//    private Integer servings; // 인분
//    private Integer cookingTime; // 조리 시간
//    private String difficulty; // 난이도

}
