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
import Fridge_Chef.team.user.domain.UserId;
import com.querydsl.core.BooleanBuilder;
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
import java.util.Optional;
import java.util.stream.Collectors;

import static Fridge_Chef.team.board.domain.QBoard.board;
import static Fridge_Chef.team.image.domain.QImage.image;
import static Fridge_Chef.team.ingredient.domain.QIngredient.ingredient;
import static Fridge_Chef.team.recipe.domain.QRecipe.recipe;
import static Fridge_Chef.team.recipe.domain.QRecipeIngredient.recipeIngredient;

@Repository
@RequiredArgsConstructor
public class RecipeDslRepository {

    private final JPAQueryFactory factory;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public Page<RecipeSearchResponse> findRecipesByIngredients(PageRequest pageable, RecipePageRequest request, List<String> must, List<String> ingredients, Optional<UserId> userId) {
        var query = factory
                .select(board)
                .from(board)
                .leftJoin(board.context.boardIngredients, recipeIngredient)
                .join(recipeIngredient.ingredient, ingredient)
                .leftJoin(board.mainImage, image);

        System.out.println(" query join size : " + query.fetch().size());
        query.groupBy(board, ingredient.name);

        System.out.println(" groupBy join size : " + query.fetch().size());

        if (must != null && !must.isEmpty()) {
            BooleanBuilder mustConditions = new BooleanBuilder();
            mustConditions.and(ingredient.name.in(must));
            query.where(mustConditions);
            query.having(ingredient.name.countDistinct().eq((long) must.size()));
        } else {
            query.where((ingredient.name.in(ingredients)));
        }

        System.out.println(" ingredients _ must  join size : " + query.fetch().size());

        applySort(query, request.getSortType());

        query.offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<String> search = new ArrayList<>(must);
        search.addAll(ingredients);

        List<RecipeSearchResponse> responses = query.fetch()
                .stream()
                .map(value -> RecipeSearchResponse.of(value, search, userId))
                .toList();

        var commit = factory
                .selectFrom(board)
                .join(board.context.boardIngredients, recipeIngredient)
                .join(recipeIngredient.ingredient, ingredient)
                .where(ingredient.name.in(must))
                .groupBy(board.id);

        return PageableExecutionUtils.getPage(responses, pageable, () -> commit.fetch().size());
    }


    private void applySort(JPAQuery<Board> query, RecipeSearchSortType sortType) {

        switch (sortType) {
            case MATCH -> query.orderBy(ingredient.name.count().desc());
            case RATING -> query.orderBy(board.totalStar.desc());
            case LIKE -> query.orderBy(board.hit.desc());
            default -> query.orderBy(board.createTime.desc());
        }
    }

    @Transactional(readOnly = true)
    public Page<RecipeSearchResponse> findRecipesByIngredient(PageRequest pageable, RecipePageRequest request, List<String> must, List<String> ingredients) {

        JPAQuery<Recipe> query = createBaseQuery(must, ingredients);

        List<RecipeSearchResponse> recipes = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
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

        return null;
    }

    private List<RecipeSearchResponse> applySort(List<RecipeSearchResponse> recipes, RecipeSearchSortType sortType) {
        List<RecipeSearchResponse> sortedRecipes = new ArrayList<>(recipes);
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
