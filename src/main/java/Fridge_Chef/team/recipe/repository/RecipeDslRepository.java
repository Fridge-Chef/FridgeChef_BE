package Fridge_Chef.team.recipe.repository;

import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResult;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static Fridge_Chef.team.image.domain.QImage.image;
import static Fridge_Chef.team.recipe.domain.QRecipe.recipe;
import static Fridge_Chef.team.recipe.domain.QRecipeIngredient.recipeIngredient;

@Repository
@RequiredArgsConstructor
public class RecipeDslRepository {

    private final JPAQueryFactory factory;

    public RecipeSearchResult findRecipesByIngredients(PageRequest page, RecipePageRequest request, List<String> ingredients) {

        JPAQuery<Recipe> query = createBaseQuery(ingredients);

        long totalCount = query.fetchCount();

        List<RecipeSearchResponse> recipes = query
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch().stream()
                .map(recipe -> convertToRecipeSearchResponse(recipe, ingredients))
                .toList();

        List<RecipeSearchResponse> sortedRecipes = applySort(recipes, request.getSortType());

        return RecipeSearchResult.builder()
                .totalCount((int) totalCount)
                .recipes(sortedRecipes)
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

        String imageUrl = recipe.getImage() != null ? recipe.getImage().getLink() : "default-image-url";

        return RecipeSearchResponse.builder()
                .name(recipe.getName())
                .imageUrl(imageUrl)
                .totalIngredients(recipeIngredientNames.size())
                .have(have)
                .without(without)
                .build();
    }

    private List<RecipeSearchResponse> applySort(List<RecipeSearchResponse> recipes, RecipeSearchSortType sortType) {

        List<RecipeSearchResponse> sortedRecipes = new ArrayList<>(recipes); // 가변 리스트 생성

        switch (sortType) {
            case MATCH -> sortedRecipes.sort((r1, r2) -> Integer.compare(r2.getHave(), r1.getHave()));
            case LIKE -> sortedRecipes.sort((r1, r2) -> Integer.compare(r2.getHit(), r1.getHit()));
            case RATING -> sortedRecipes.sort((r1, r2) -> Double.compare(r2.getTotalStar(), r1.getTotalStar()));
        }

        return sortedRecipes;
    }
}
