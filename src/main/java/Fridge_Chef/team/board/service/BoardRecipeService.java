package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.*;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.repository.ContextRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardRecipeService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardUserEventRepository boardUserEventRepository;
    private final ContextRepository contextRepository;
    private final IngredientRepository ingredientRepository;
    private final ImageService imageService;
    private final BoardIngredientService boardIngredientService;

    @Transactional
    public Board create(UserId userId, BoardByRecipeRequest request) {
        User user = findByUserId(userId);

        Image image = imageService.imageUpload(user.getUserId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        Context context = contextRepository.save(Context.formMyUserRecipe(
                request.getDishTime(), request.getDishLevel(), request.getDishCategory(),
                ingredients, descriptions));

        Board board = boardRepository.save(new Board(user, request.getDescription(), request.getName(), context, image, BoardType.USER));
        BoardUserEvent event = new BoardUserEvent(board, user);
        boardUserEventRepository.save(event);
        log.info("레시피 등록 "+ request.getName() +", user "+ user.getUsername());
        return board;
    }

    public Ingredient getIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
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
    @Transactional
    public Board update(UserId userId, BoardByRecipeUpdateRequest request,
                        List<RecipeIngredient> ingredients, List<Description> descriptions,
                        Image mainImage) {
        findByUserId(userId);
        Board board = boardRepository.findById(request.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));
        validBoardUser(board, userId);

        board.updateMainImage(mainImage);
        board.updateTitle(request.getTitle());
        board.updateContext(ingredients, descriptions, request.getDishTime(), request.getDishLevel(), request.getDishCategory());
        return board;
    }

    private void validBoardUser(Board board, UserId userId) {
        if (!board.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.BOARD_NOT_USER_CREATE);
        }
    }

    private User findByUserId(UserId userId) {
        return userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
