package Fridge_Chef.team.recipe.dump;

import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.recipe.repository.RecipeJdbcRepository;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Component
@RequiredArgsConstructor
@org.springframework.context.annotation.Profile({"dev", "prod"})
public class RecipeMetadataInit {
    private static final Pattern CSV_LINE_PATTERN = Pattern.compile("\"([^\"]*)\"|([^,]+)");
    private static final Pattern INGREDIENTS_SPLIT_PATTERN = Pattern.compile("\\s*,\\s*(?=(?:[^()]*\\([^()]*\\))*[^()]*$)");
    private static final Pattern INGREDIENT_NAME_PATTERN = Pattern.compile("(.*?)(\\s*\\(.*?\\))?$");
    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RecipeJdbcRepository recipeJdbcRepository;

    @Transactional
    @PostConstruct
    public void init() throws IOException {
        String email = "recipeUser@fridge.chef";

        if (userRepository.existsByProfileEmail(email)) {
            return;
        }

        log.info("recipe init()");
        User user = createAdminUser(email);
        List<List<String>> json = getScv();

        createBasicRecipes(json, user, initIngredient(json));
    }

    private User createAdminUser(String email) {
        UserId userId = UserId.create();
        Profile profile = new Profile(null, email, "fridge chef", null);
        User user = new User(userId, profile, Role.ADMIN);

        userRepository.save(user);

        return user;
    }

    private List<Ingredient> initIngredient(List<List<String>> json) {
        List<String> names = new ArrayList<>();

        for (List<String> datas : json) {
            names.addAll(Arrays.stream(INGREDIENTS_SPLIT_PATTERN.split(datas.get(5).trim()))
                    .map(str -> match(INGREDIENT_NAME_PATTERN, str))
                    .toList()
            );
        }

        List<Ingredient> ingredients = names.stream()
                .distinct()
                .map(Ingredient::new)
                .toList();

        return ingredientRepository.saveAll(ingredients);
    }

    private void createBasicRecipes(List<List<String>> json, User user, List<Ingredient> ingredients) {
        recipeJdbcRepository.bulkInsertBoardsWithJdbcTemplate(user, json, ingredients);
    }

    private List<List<String>> getScv() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:basic_recipes.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
        List<List<String>> json = new ArrayList<>(new ArrayList<>());
        String line;
        while ((line = reader.readLine()) != null) {
            json.add(parseLine(line));
        }
        return json;
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

    private String match(Pattern pattern, String part) {
        Matcher matcher = pattern.matcher(part);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return part;
    }
}
