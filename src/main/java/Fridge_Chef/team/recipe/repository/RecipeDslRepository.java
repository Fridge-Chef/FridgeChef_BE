package Fridge_Chef.team.recipe.repository;

import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResult;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static Fridge_Chef.team.image.domain.QImage.image;
import static Fridge_Chef.team.recipe.domain.QRecipe.recipe;
import static Fridge_Chef.team.recipe.domain.QRecipeIngredient.recipeIngredient;

@Repository
@RequiredArgsConstructor
public class RecipeDslRepository {

    private final JPAQueryFactory factory;

    public RecipeSearchResult findRecipesByIngredients(PageRequest page, List<String> ingredients) {

        JPAQuery<Recipe> query = createBaseQuery(ingredients);

        int totalCount = query.fetch().size();

        List<RecipeSearchResponse> recipes = query
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch().stream()
                .map(recipe -> convertToRecipeSearchResponse(recipe, ingredients)) // ingredients를 파라미터로 전달
                .sorted((r1, r2) -> Integer.compare(r2.getHave(), r1.getHave()))
                .toList();

        return RecipeSearchResult.builder()
                .totalCount(totalCount)
                .recipes(recipes)
                .build();
    }

    private JPAQuery<Recipe> createBaseQuery(List<String> ingredients) {

        return factory
                .selectFrom(recipe)
                .leftJoin(recipe.recipeIngredients, recipeIngredient)
                .leftJoin(recipe.image, image)
                .where(recipeIngredient.ingredient.name.in(ingredients))
                .groupBy(recipe.id)
                .having(recipe.recipeIngredients.size().goe(1));
    }

    private RecipeSearchResponse convertToRecipeSearchResponse(Recipe recipe, List<String> ingredients) {

        List<String> recipeIngredientNames = recipe.getRecipeIngredients().stream()
                .map(ri -> ri.getIngredient().getName())
                .toList();

        List<String> without = recipeIngredientNames.stream()
                .filter(ri -> !ingredients.contains(ri))
                .collect(Collectors.toList());

        int have = recipeIngredientNames.size() - without.size();

        return RecipeSearchResponse.builder()
                .name(recipe.getName())
                .imageUrl(recipe.getImage().getLink())
                .totalIngredients(recipeIngredientNames.size())
                .have(have)
                .without(without)
                .build();
    }
}
