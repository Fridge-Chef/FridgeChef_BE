package Fridge_Chef.team.recipe.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardType;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.ContextRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.ingredient.rest.response.IngredientResponse;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.Difficult;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeDslRepository;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.recipe.rest.request.RecipeCreateRequest;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResult;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final IngredientService ingredientService;
    private final UserService userService;

    private final RecipeRepository recipeRepository;
    private final RecipeDslRepository recipeDslRepository;
    private final DescriptionRepository descriptionRepository;
    private final ImageRepository imageRepository;
    private final ContextRepository contextRepository;
    private final BoardRepository boardRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    @Transactional
    public void createMyRecipe(UserId userId, RecipeCreateRequest request) {

        User user = userService.findByUser(userId);

        if (recipeRepository.existsByName(request.getName())) {
            throw new ApiException(ErrorCode.RECIPE_NAME_ALREADY_EXISTS);
        }

        Image image = Image.outUri(request.getImageUrl());
        imageRepository.save(image);

        Difficult difficult = Difficult.valueOf(request.getDifficult());
        List<Description> descriptions = insertDescriptions(request.getDescriptions());
        List<RecipeIngredient> recipeIngredients = insertRecipeIngredients(request.getRecipeIngredients());

        Recipe recipe = Recipe.builder()
                .name(request.getName())
                .intro(request.getIntro())
                .image(image)
                .cookTime(request.getCookTime())
                .difficult(difficult)
                .descriptions(descriptions)
                .recipeIngredients(recipeIngredients)
                .build();

        recipeRepository.save(recipe);

        recipeToBoard(user, recipe);
    }

    @Transactional(readOnly = true)
    public RecipeSearchResult searchRecipe(RecipePageRequest request, List<String> ingredients) {

        PageRequest page = PageRequest.of(request.getPage(), request.getSize());
        RecipeSearchResult response = recipeDslRepository.findRecipesByIngredients(page, request, ingredients);

        if (response.getRecipes().isEmpty()) {
            throw new ApiException(ErrorCode.RECIPE_NOT_FOUND);
        }

        return response;
    }

    private List<Description> insertDescriptions(List<Description> requestDescriptions) {

        List<Description> descriptions = new ArrayList<>();

        for (Description requestDescription : requestDescriptions) {
            String manual = requestDescription.getDescription();
            Image image = requestDescription.getImage();

            if (image != null) {
                imageRepository.save(image);
            }

            Description description = new Description(manual, image);
            descriptions.add(description);
        }

        descriptionRepository.saveAll(descriptions);

        return descriptions;
    }

    private List<RecipeIngredient> insertRecipeIngredients(List<RecipeIngredient> requestRecipeIngredients) {

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        for (RecipeIngredient requestIngredient : requestRecipeIngredients) {
            String ingredientName = requestIngredient.getIngredient().getName();
            String quantity = requestIngredient.getQuantity();

            Ingredient ingredient = ingredientService.getOrCreate(ingredientName);
            RecipeIngredient recipeIngredient = RecipeIngredient.ofMyRecipe(ingredient, quantity);
            recipeIngredients.add(recipeIngredient);
        }

        recipeIngredientRepository.saveAll(recipeIngredients);

        return recipeIngredients;
    }

    private void recipeToBoard(User user, Recipe recipe) {

        Context context = Context.formMyUserRecipe(recipe.getRecipeIngredients(), recipe.getDescriptions());
        contextRepository.save(context);

        Board board = new Board(user, recipe.getIntro(), recipe.getName(), context, recipe.getImage(), BoardType.USER);
        boardRepository.save(board);
    }

    private RecipeResponse recipeToDto(Recipe recipe) {

        List<IngredientResponse> ingredients = recipe.getRecipeIngredients().stream()
                .map(recipeIngredient -> IngredientResponse.builder()
                        .name(recipeIngredient.getIngredient().getName())
                        .quantity(recipeIngredient.getQuantity())
                        .build())
                .toList();

        return RecipeResponse.builder()
                .name(recipe.getName())
//                .ingredients(ingredients)
//                .manuals(recipe.getManuals())
//                .imageUrl(recipe.getImage().getLink())
                .build();
    }
}
