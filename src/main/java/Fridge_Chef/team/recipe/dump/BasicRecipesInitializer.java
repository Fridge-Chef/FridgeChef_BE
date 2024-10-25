package Fridge_Chef.team.recipe.dump;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.ContextRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.Difficult;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.user.domain.Profile;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile({"prod", "dev"})
public class BasicRecipesInitializer {

    private final ResourceLoader resourceLoader;

    private final IngredientService ingredientService;

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final DescriptionRepository descriptionRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ContextRepository contextRepository;
    private final BoardRepository boardRepository;

    private static final Pattern CSV_LINE_PATTERN = Pattern.compile("\"([^\"]*)\"|([^,]+)");
    private static final Pattern INGREDIENTS_SPLIT_PATTERN = Pattern.compile("\\s*,\\s*(?=(?:[^()]*\\([^()]*\\))*[^()]*$)");
    private static final Pattern INGREDIENT_NAME_PATTERN = Pattern.compile("(.*?)(\\s*\\(.*?\\))?$");
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("\\((.*?)\\)");



    @PostConstruct
    public void init() throws IOException{
        String email ="recipeUser@fridge.chef";
        if(!userRepository.existsByProfileEmail(email)){
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
        while ((line = reader.readLine()) != null) {
            List<String> fields = parseLine(line);

            Recipe recipe = createRecipe(fields);
            saveRecipeWithIngredients(recipe);
            recipeToBoard(user, recipe);
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
        int cookTime = Integer.parseInt(fields.get(2).replace("분", ""));
        Difficult difficult = Difficult.of(fields.get(3));
        String intro = fields.get(4);

        String ingredients = fields.get(5);
        List<RecipeIngredient> recipeIngredients = extractRecipeIngredients(ingredients);
        List<Description> descriptions = extractDescriptions(fields);

        Recipe recipe = Recipe.builder()
                .name(name)
                .category(category)
                .cookTime(cookTime)
                .difficult(difficult)
                .intro(intro)
                .recipeIngredients(recipeIngredients)
                .descriptions(descriptions)
                .build();

        recipeRepository.save(recipe);

        return recipe;
    }

    private List<RecipeIngredient> extractRecipeIngredients(String ingredients) {

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        String[] parts = INGREDIENTS_SPLIT_PATTERN.split(ingredients);
        for (String part : parts) {
            part = part.trim();

            String ingredientName = extractIngredientName(part);
            String quantity = extractQuantity(part);

            Ingredient ingredient = ingredientService.getOrCreate(ingredientName);

            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .ingredient(ingredient)
                    .quantity(quantity)
                    .build();

            recipeIngredients.add(recipeIngredient);
        }

        recipeIngredientRepository.saveAll(recipeIngredients);

        return recipeIngredients;
    }

    private List<Description> extractDescriptions(List<String> fields) {

        List<Description> descriptions = new ArrayList<>();

        for (int i = 6; i < fields.size(); i++) {
            String manual = fields.get(i);

            Description description = new Description(manual, null);
            descriptions.add(description);
        }

        descriptionRepository.saveAll(descriptions);

        return descriptions;
    }

    private String extractIngredientName(String part) {

        Matcher matcher = INGREDIENT_NAME_PATTERN.matcher(part);

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

    private void saveRecipeWithIngredients(Recipe recipe) {

        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.setRecipe(recipe);
        }

        recipeIngredientRepository.saveAll(recipe.getRecipeIngredients());
    }

    private void recipeToBoard(User user, Recipe recipe) {

        Context context = Context.formMyUserRecipe(recipe.getRecipeIngredients(), recipe.getDescriptions());
        context = contextRepository.save(context);

        Board board = Board.from(user, recipe);
        board.setContext(context);
        boardRepository.save(board);
    }
}
