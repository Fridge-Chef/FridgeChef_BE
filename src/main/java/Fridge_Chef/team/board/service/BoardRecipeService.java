package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.*;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.BoardUserEventRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.service.request.RecipeIngredientDto;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardRecipeService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardUserEventRepository boardUserEventRepository;
    private final ImageService imageService;
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final BoardIngredientService boardIngredientService;

    @Transactional
    public Board create(UserId userId, BoardByRecipeRequest request) {
        User user = findByUserId(userId);

        Image image = imageService.imageUpload(user.getUserId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.getUserId(), request);
        List<RecipeIngredient> ingredients = findOrCreate(request.getRecipeIngredients());

        Context context = Context.formMyUserRecipe(
                request.getDishTime(), request.getDishLevel(), request.getDishCategory(),
                ingredients, descriptions);

        Board board = boardRepository.save(new Board(user, request.getDescription(), request.getName(), context, image, BoardType.USER));
        BoardUserEvent event = new BoardUserEvent(board, user);
        boardUserEventRepository.save(event);
        log.info("레시피 등록 " + request.getName() + ", user " + user.getUsername());
        return board;
    }

    @Transactional
    public List<RecipeIngredient> findOrCreate(List<BoardByRecipeRequest.RecipeIngredient> recipeIngredients) {
        return recipeIngredients.stream()
                .map(request -> findOrSaveIngredient(request.getName(), request.getDetails()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Board update(UserId userId, BoardByRecipeUpdateRequest request) {
        log.info("레시피 수정 " + request.getTitle() + " 소개 : " + request.getDescription());
        Board board = boardRepository.findById(request.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        validBoardUser(board, userId);
        board.getContext().clearRecipe();

        if (request.isMainImageChange()) {
            Image mainImage = imageService.uploadImageWithId(userId, request.isMainImageChange(),
                    board.getMainImageId(), request.getMainImage());
            board.updateMainImage(mainImage);
        }

        for (var ingredient : request.getRecipeIngredients()) {
            RecipeIngredient recipeIngredient = boardIngredientService.findOrSaveIngredient(new RecipeIngredientDto(ingredient.getName(), ingredient.getDetails()));
            board.getContext().addRecipeIngredient(recipeIngredient);
        }

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(userId, request,board.getContext().getDescriptions());
        board.updateContext(descriptions, request.getDescription(), request.getTitle(), request.getDishTime(), request.getDishLevel(), request.getDishCategory());
        return board;
    }

    private RecipeIngredient findOrSaveIngredient(String name, String details) {
        Ingredient ingredient = updateRecipeIngredient(name);
        RecipeIngredient findRecipeIngredient = RecipeIngredient.ofMyRecipe(ingredient, details);
        return recipeIngredientRepository.save(findRecipeIngredient);
    }

    private Ingredient updateRecipeIngredient(String name) {
        return ingredientRepository.findByName(name)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(name)));
    }

    private void validBoardUser(Board board, UserId userId) {
        if (!board.getUser().getUserId().equals(userId)) {
            throw new ApiException(ErrorCode.BOARD_NOT_USER_CREATE);
        }
    }

    private User findByUserId(UserId userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
