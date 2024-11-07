package Fridge_Chef.team.recipe.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    public Page<RecipeSearchResponse> findRecipesByIngredients(PageRequest pageable, RecipePageRequest request, List<String> must, List<String> ingredients) {

        JPAQuery<Recipe> query = createBaseQuery(must, ingredients);

        List<RecipeSearchResponse> recipes = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch().stream()
                .map(recipe -> convertToRecipeSearchResponse(recipe, must, ingredients))
                .toList();

        List<RecipeSearchResponse> sortedRecipes = applySort(recipes, request.getSortType());

        return PageableExecutionUtils.getPage(sortedRecipes, pageable, () -> query.fetch().size());
    }

    private JPAQuery<Recipe> createBaseQuery(List<String> must, List<String> ingredients) {
        JPAQuery<Recipe> query = factory
                .selectFrom(recipe)
                .leftJoin(recipe.recipeIngredients, recipeIngredient)
                .leftJoin(recipe.image, image)
                .groupBy(recipe.id, recipe.category, recipe.cookTime, recipe.difficult, recipe.createTime,
                        recipe.deleteStatus, recipe.image.id, recipe.intro, recipe.name, recipe.updateTime);

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
        String imageUrl = recipe.getImage() != null ? recipe.getImage().getLink() : "";

        Board board = findBoardFromRecipe(recipe);
        if (board == null) {
            throw new ApiException(ErrorCode.BOARD_NOT_FOUND);
        }

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

    private Board findBoardFromRecipe(Recipe recipe) {

        List<Board> boardList = boardRepository.findByTitle(recipe.getName())
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        for (Board board : boardList) {
            Context context = board.getContext();

            if (context.getDishCategory().equals(recipe.getCategory())
            && context.getDishLevel().equals(String.valueOf(recipe.getDifficult()))
            && context.getDishTime().equals(String.valueOf(recipe.getCookTime()))) {
                return board;
            }
        }

        return null;
    }
}
