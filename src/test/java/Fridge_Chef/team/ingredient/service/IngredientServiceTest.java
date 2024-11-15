package Fridge_Chef.team.ingredient.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.rest.response.IngredientSearchResponse;
import Fridge_Chef.team.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("재료")
public class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    private final static String name = "테스트";

//    @Test
    @DisplayName("생성")
    @Transactional
    void create() {
        Ingredient ingredient = ingredientService.createIngredient(name);

        assertThat(ingredient.getName()).isEqualTo(name);
    }

//    @Test
    @DisplayName("조회")
    @Transactional
    void getIngredient() {
        givenIngredients();

        Ingredient fromRepository = ingredientRepository.findByName(name).orElseThrow();
        Ingredient fromService = ingredientService.getIngredient(name);

        assertThat(fromService).isNotNull();
        assertThat(fromService).isEqualTo(fromRepository);
    }

//    @Test
    @DisplayName("키워드 조회")
    @Transactional
    void find() {
        givenIngredients();

        IngredientSearchResponse response = ingredientService.searchIngredients("스");

        assertThat(response.getIngredientNames().size()).isEqualTo(2);
    }

//    @Test
    @DisplayName("키워드 조회 실패")
    @Transactional
    void not_found() {
        givenIngredients();

        assertThatThrownBy(() -> ingredientService.searchIngredients("test"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining(ErrorCode.INGREDIENT_NOT_FOUND.getMessage());
    }

//    @Test
    @DisplayName("모든 재료 조회")
    @Transactional(readOnly = true)
    void finds() {
        givenIngredients();

        List<Ingredient> fromRepository = ingredientRepository.findAll();
        IngredientSearchResponse response = ingredientService.findAllIngredients();

        assertThat(response.getIngredientNames().size()).isEqualTo(fromRepository.size());
    }

    private void givenIngredients() {

        Ingredient ingredient1 = new Ingredient("소금");
        Ingredient ingredient2 = new Ingredient("후추");
        Ingredient ingredient3 = new Ingredient("굴소스");
        Ingredient ing = new Ingredient(name);

        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        ingredientRepository.save(ing);
    }
}
