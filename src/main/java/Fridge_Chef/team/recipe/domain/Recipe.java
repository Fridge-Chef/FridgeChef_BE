package Fridge_Chef.team.recipe.domain;


import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class Recipe  extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String name; // 레시피 이름
    private String category; // 레시피 카테고리 (한식, 양식 등)
    private String instructions; // 요리 순서
    private String imageUrl; // 레시피 이미지 URL
    private Integer servings; // 인분
    private Integer cookingTime; // 조리 시간
    private String difficulty; // 난이도

}
