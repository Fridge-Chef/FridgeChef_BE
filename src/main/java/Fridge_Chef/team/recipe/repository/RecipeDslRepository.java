package Fridge_Chef.team.recipe.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResult;
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
    private final BoardRepository boardRepository;

    public RecipeSearchResult findRecipesByIngredients(PageRequest page, RecipePageRequest request, List<String> must, List<String> ingredients) {

        JPAQuery<Recipe> query = createBaseQuery(must, ingredients);

        long totalCount = query.fetchCount();

        List<RecipeSearchResponse> recipes = query
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetch().stream()
                .map(recipe -> convertToRecipeSearchResponse(recipe, must, ingredients))
                .toList();

        List<RecipeSearchResponse> sortedRecipes = applySort(recipes, request.getSortType());

        return RecipeSearchResult.builder()
                .totalCount((int) totalCount)
                .recipes(sortedRecipes)
                .build();
    }

    private JPAQuery<Recipe> createBaseQuery(List<String> must, List<String> ingredients) {

        JPAQuery<Recipe> query = factory
                .selectFrom(recipe)
                .leftJoin(recipe.recipeIngredients, recipeIngredient)
                .leftJoin(recipe.image, image)
                .groupBy(recipe.id);

        if (must != null && !must.isEmpty()) {
            query.where(recipeIngredient.ingredient.name.in(must))
                    .having(recipeIngredient.ingredient.name.countDistinct().goe(must.size()));
        }

        if (ingredients != null && !ingredients.isEmpty()) {
            query.where(recipeIngredient.ingredient.name.in(ingredients).or(recipeIngredient.ingredient.name.in(must)));
        }

        return query;
    }

    private RecipeSearchResponse convertToRecipeSearchResponse(Recipe recipe, List<String> must, List<String> ingredients) {

        List<String> recipeIngredientNames = recipe.getRecipeIngredients().stream()
                .map(ri -> ri.getIngredient().getName())
                .toList();

        List<String> without = recipeIngredientNames.stream()
                .filter(ri -> !ingredients.contains(ri) && !must.contains(ri))
                .collect(Collectors.toList());

        int have = recipeIngredientNames.size() - without.size();

        Board board = boardRepository.findByTitle(recipe.getName())
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        String imageUrl = recipe.getImage() != null ? recipe.getImage().getLink() : "default-image-url";

        return RecipeSearchResponse.builder()
                .name(recipe.getName())
                .imageUrl(imageUrl)
                .totalIngredients(recipeIngredientNames.size())
                .hit(board.getHit())
                .totalStar(board.getTotalStar())
                .have(have)
                .without(without)
                .boardId(board.getId())
                .build();
    }

    private List<RecipeSearchResponse> applySort(List<RecipeSearchResponse> recipes, RecipeSearchSortType sortType) {

        List<RecipeSearchResponse> sortedRecipes = new ArrayList<>(recipes);

        switch (sortType) {
            case MATCH -> sortedRecipes.sort((r1, r2) -> Integer.compare(r2.getHave(), r1.getHave()));
            case LIKE -> sortedRecipes.sort((r1, r2) -> Integer.compare(r2.getHit(), r1.getHit()));
            case RATING -> sortedRecipes.sort((r1, r2) -> Double.compare(r2.getTotalStar(), r1.getTotalStar()));
        }

        return sortedRecipes;
    }
}
