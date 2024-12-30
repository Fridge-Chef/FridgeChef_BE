package Fridge_Chef.team.recipe.repository;

import Fridge_Chef.team.board.domain.BoardType;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.recipe.domain.Difficult;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static Fridge_Chef.team.common.entity.OracleBoolean.F;


@Slf4j
@Repository
@RequiredArgsConstructor
public class RecipeJdbcRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final Pattern INGREDIENTS_SPLIT_PATTERN = Pattern.compile("\\s*,\\s*(?=(?:[^()]*\\([^()]*\\))*[^()]*$)");
    private static final Pattern INGREDIENT_NAME_PATTERN = Pattern.compile("(.*?)(\\s*\\(.*?\\))?$");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("\\((.*?)\\)");

    @Transactional
    public void bulkInsertBoardsWithJdbcTemplate(User user, List<List<String>> fieldLists, List<Ingredient> ingredients) {
        List<Object[]> imageBatchArgs = new ArrayList<>();
        List<Object[]> boardBatchArgs = new ArrayList<>();
        List<Object[]> boardIngredientsBatchArgs = new ArrayList<>();
        List<Object[]> boardDescriptionsBatchArgs = new ArrayList<>();

        int batchSize = fieldLists.size();
        LocalDateTime now = LocalDateTime.now();
        String userId = user.getId().toString().toUpperCase().replace("-", "");

        List<Long> imageIds = jdbcTemplate.query("SELECT image_seq.NEXTVAL FROM DUAL CONNECT BY LEVEL <= ?",
                new Object[]{batchSize},
                (rs, rowNum) -> rs.getLong(1)
        );

        List<Long> boardIds = jdbcTemplate.query("SELECT board_seq.NEXTVAL FROM DUAL CONNECT BY LEVEL <= ?",
                new Object[]{batchSize},
                (rs, rowNum) -> rs.getLong(1)
        );

        for (int i = 0; i < fieldLists.size(); i++) {
            List<String> fields = fieldLists.get(i);
            String name = fields.get(0);
            String category = fields.get(1);
            String cookTime = fields.get(2);
            Difficult difficult = Difficult.of(fields.get(3));
            String intro = fields.get(4);
            String ingredient = fields.get(5);

            List<RecipeIngredient> recipeIngredients = extractRecipeIngredients(ingredient, ingredients);
            List<Description> descriptions = extractDescriptions(fields);

            Context context = Context.formMyUserRecipe(cookTime, difficult.getValue(), category, recipeIngredients, descriptions);
            String imageUrl = "https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/" + i + "_recipe.png";

            Image image = Image.outUri(imageUrl);
            Long imageId = imageIds.get(i);
            imageBatchArgs.add(new Object[]{imageId, image.getPath(), image.getName(), image.getType().name()});

            Long boardId = boardIds.get(i);
            boardBatchArgs.add(new Object[]{
                    boardId,
                    userId,
                    imageId,
                    now,
                    now,
                    context.getPathIngredient(),
                    F.name(),
                    context.getDishCategory(),
                    context.getDishLevel(),
                    context.getDishTime(),
                    intro,
                    imageUrl,
                    name,
                    BoardType.OPEN_API.name(),
                    0,
                    0,
                    0
            });

            for (RecipeIngredient recipeIngredient : recipeIngredients) {
                boardIngredientsBatchArgs.add(new Object[]{boardId, recipeIngredient.getIngredient().getId(), recipeIngredient.getQuantity()});
            }

            for (Description description : descriptions) {
                boardDescriptionsBatchArgs.add(new Object[]{boardId, description.getDescription()});
            }
        }
        jdbcTemplate.batchUpdate("INSERT INTO images (id, path, names, type) VALUES (?, ?, ?, ?)", imageBatchArgs);
        jdbcTemplate.batchUpdate("INSERT INTO board " +
                "(id, user_id, main_image_id, create_time, update_time, path_ingredient, delete_status, " +
                "dish_category, dish_level, dish_time, introduction, path_main_image, title, type, count, hit, total_star) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", boardBatchArgs
        );

        jdbcTemplate.batchUpdate("INSERT INTO recipe_ingredient (board_id, ingredient_id, quantity) VALUES (?, ?, ?)", boardIngredientsBatchArgs);
        jdbcTemplate.batchUpdate("INSERT INTO description (board_id, description) VALUES (?, ?)", boardDescriptionsBatchArgs);
    }

    private List<RecipeIngredient> extractRecipeIngredients(String ingredient, List<Ingredient> ingredients) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        String[] parts = INGREDIENTS_SPLIT_PATTERN.split(ingredient);
        for (String part : parts) {
            part = part.trim();
            String ingredientName = match(INGREDIENT_NAME_PATTERN, part);
            String quantity = extractQuantity(part);
            recipeIngredients.add(RecipeIngredient.ofMyRecipe(ingredients.stream()
                    .filter(name -> name.getName().equals(ingredientName))
                    .findFirst()
                    .orElseThrow(() -> new ApiException(ErrorCode.INGREDIENT_CATEGORY_INVALID)), quantity));
        }
        return recipeIngredients;
    }

    private List<Description> extractDescriptions(List<String> fields) {
        List<Description> descriptions = new ArrayList<>();
        for (int i = 6; i < fields.size(); i++) {
            String manual = fields.get(i);
            Description description = new Description(manual, null);
            descriptions.add(description);
        }
        return descriptions;
    }

    private String match(Pattern pattern, String part) {
        Matcher matcher = pattern.matcher(part);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return part;
    }

    private String extractQuantity(String part) {
        Matcher matcher = QUANTITY_PATTERN.matcher(part);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "X";
    }
}
