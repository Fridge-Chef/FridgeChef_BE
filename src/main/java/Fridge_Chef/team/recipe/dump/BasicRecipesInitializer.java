package Fridge_Chef.team.recipe.dump;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.Difficult;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.Profile;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicRecipesInitializer {
    private static final Pattern CSV_LINE_PATTERN = Pattern.compile("\"([^\"]*)\"|([^,]+)");
    private static final Pattern INGREDIENTS_SPLIT_PATTERN = Pattern.compile("\\s*,\\s*(?=(?:[^()]*\\([^()]*\\))*[^()]*$)");
    private static final Pattern INGREDIENT_NAME_PATTERN = Pattern.compile("(.*?)(\\s*\\(.*?\\))?$");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("\\((.*?)\\)");

    private final IngredientService ingredientService;
    private final ImageRepository imageRepository;
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardUserEventRepository boardUserEventRepository;


    @PostConstruct
    public void init() throws IOException {
        log.info("recipe init()");
        String email = "recipeUser@fridge.chef";
        if (!userRepository.existsByProfileEmail(email)) {
            User user = createAdminUser(email);
            createBasicRecipes(user);
        }
    }

    @Transactional
    public User createAdminUser(String email) {
        UserId userId = UserId.create();
        Profile profile = new Profile(null, email, "fridge chef", null);
        User user = new User(userId, profile, Role.ADMIN);

        userRepository.save(user);

        return user;
    }

    @Transactional
    public void createBasicRecipes(User user) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:basic_recipes.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

        String line;
        int index = 0;
        while ((line = reader.readLine()) != null) {
            List<String> fields = parseLine(line);
            recipeToBoard(user, createRecipe(fields), index++);
            log.info("board insert >");
        }
    }

    private List<String> parseLine(String line) {
        List<String> result = new ArrayList<>();
        Matcher matcher = CSV_LINE_PATTERN.matcher(line);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                result.add(matcher.group(1));
            } else {
                result.add(matcher.group(2).trim());
            }
        }
        return result;
    }

    private Recipe createRecipe(List<String> fields) {
        String name = fields.get(0);
        String category = fields.get(1);
        String cookTime = fields.get(2);
        Difficult difficult = Difficult.of(fields.get(3));
        String intro = fields.get(4);
        String ingredients = fields.get(5);

        List<RecipeIngredient> recipeIngredients = extractRecipeIngredients(ingredients);
        List<Description> descriptions = extractDescriptions(fields);
        return Recipe.builder()
                .name(name)
                .category(category)
                .cookTime(cookTime)
                .difficult(difficult)
                .intro(intro)
                .recipeIngredients(recipeIngredients)
                .descriptions(descriptions)
                .build();
    }

    private List<RecipeIngredient> extractRecipeIngredients(String ingredients) {

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        String[] parts = INGREDIENTS_SPLIT_PATTERN.split(ingredients);
        for (String part : parts) {
            part = part.trim();
            String ingredientName = match(INGREDIENT_NAME_PATTERN, part);
            String quantity = extractQuantity(part);
            Ingredient ingredient = ingredientService.getOrCreate(ingredientName);

            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .ingredient(ingredient)
                    .quantity(quantity)
                    .build();

            recipeIngredients.add(recipeIngredient);
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

    private void recipeToBoard(User user, Recipe recipe, int index) {
        Context context = Context.formMyUserRecipe(recipe.getCookTime(), String.valueOf(recipe.getDifficult()), recipe.getCategory(),
                toRecipeIngredient(recipe.getRecipeIngredients()), toDescriptions(recipe.getDescriptions()));
        Image image = Image.outUri("https://objectstorage.ap-chuncheon-1.oraclecloud.com/p/RO5Ur4yw-jzifHvgLdMG4nkUmU_UJpzy3YQnWXaJnTIAygJO3qDzSwMy0ulHEwxt/n/axqoa2bp7wqg/b/fridge/o/" + index + "_recipe.png");
        imageRepository.save(image);

        Board board = Board.from(user, recipe.getIntro(), recipe.getName(), context, image);
        boardRepository.save(board);
        boardUserEventRepository.save(new BoardUserEvent(board, user));
    }

    public List<Description> toDescriptions(List<Description> descriptions) {
        List<Description> result = new ArrayList<>();
        for (Description description : descriptions) {
            result.add(new Description(description.getDescription(), description.getImage()));
        }
        return result;
    }

    public List<RecipeIngredient> toRecipeIngredient(List<RecipeIngredient> recipeIngredients) {
        List<RecipeIngredient> result = new ArrayList<>();
        for (RecipeIngredient value : recipeIngredients) {
            result.add(new RecipeIngredient(value.getIngredient(), value.getQuantity()));
        }
        return result;
    }
}
