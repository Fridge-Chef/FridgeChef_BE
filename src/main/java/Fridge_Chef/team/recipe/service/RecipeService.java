package Fridge_Chef.team.recipe.service;

import Fridge_Chef.team.board.domain.*;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.repository.ContextRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.Difficult;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeDslRepository;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.recipe.rest.request.RecipeCreateRequest;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final UserService userService;
    private final ImageService imageService;
    private final IngredientService ingredientService;

    private final RecipeRepository recipeRepository;
    private final RecipeDslRepository recipeDslRepository;
    private final DescriptionRepository descriptionRepository;
    private final ContextRepository contextRepository;
    private final BoardRepository boardRepository;
    private final BoardUserEventRepository boardUserEventRepository;

    @Transactional
    public void createMyRecipe(UserId userId, RecipeCreateRequest request,
                               List<RecipeIngredient> recipeIngredients,
                               List<Description> descriptions) {

        User user = userService.findByUser(userId);

        Image mainImage = (request.getMainImage() != null) ? imageService.imageUpload(userId, request.getMainImage()) : null;
        Difficult difficult = Difficult.valueOf(request.getDifficult());

        Recipe recipe = Recipe.builder()
                .name(request.getName())
                .intro(request.getIntro())
                .image(mainImage)
                .cookTime(request.getCookTime())
                .difficult(difficult)
                .category(request.getCategory())
                .descriptions(descriptions)
                .recipeIngredients(recipeIngredients)
                .build();

        recipeRepository.save(recipe);

        recipeToBoard(user, recipe);
    }

    @Transactional(readOnly = true)
    public Page<RecipeSearchResponse> searchRecipe(RecipePageRequest request, List<String> must, List<String> ingredients, Optional<UserId>userId) {

        PageRequest page = PageRequest.of(request.getPage(), request.getSize());
        Page<RecipeSearchResponse> response = recipeDslRepository.findRecipesByIngredients(page, request, must, ingredients,userId);

        if (response.getSize() == 0) {
            throw new ApiException(ErrorCode.RECIPE_NOT_FOUND);
        }

        return response;
    }

    @Transactional
    public List<Description> createDescriptions(UserId userId, List<RecipeCreateRequest.Description> request) {
        List<Description> descriptions = request.stream()
                .map(description -> {
                    String manual = description.getContent();
                    Image image = (description.getImage() != null) ? imageService.imageUpload(userId, description.getImage()) : null;
                    return new Description(manual, image);
                })
                .collect(Collectors.toList());

        return descriptionRepository.saveAll(descriptions);
    }

    private void recipeToBoard(User user, Recipe recipe) {
        List<RecipeIngredient> ingredients = recipe.getRecipeIngredients();
        List<Description> descriptions = recipe.getDescriptions();

        Context context = Context.formMyUserRecipe(
                recipe.getCookTime(), recipe.getDifficult().name(), recipe.getCategory(),
                ingredients, descriptions);
        contextRepository.save(context);

        Board board = new Board(user, recipe.getIntro(), recipe.getName(), context, recipe.getImage(), BoardType.USER);
        boardRepository.save(board);

        BoardUserEvent event = new BoardUserEvent(board, user);
        boardUserEventRepository.save(event);
    }
}
