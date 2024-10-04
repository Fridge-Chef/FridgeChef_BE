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
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardRecipeService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final BoardUserEventRepository boardUserEventRepository;
    private final ContextRepository contextRepository;

    @Transactional
    public Board create(UserId userId, BoardByRecipeRequest request,
                        List<RecipeIngredient> recipeIngredient,
                        List<Description> descriptions,
                        Image image) {
        User user = findByUserId(userId);
        Context context = contextRepository.save(Context.formMyUserRecipe(recipeIngredient, descriptions));
        Board board = boardRepository.save(new Board(user, request.getDescription(),request.getName(), context, image, BoardType.USER));
        BoardUserEvent event = new BoardUserEvent(board, user);
        boardUserEventRepository.save(event);
        return board;
    }

    @Transactional
    public Board update(UserId userId, BoardByRecipeUpdateRequest request,
                        List<RecipeIngredient> ingredients, List<Description> descriptions,
                        Image mainImage) {
        findByUserId(userId);
        Board board = boardRepository.findById(request.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.BOARD_NOT_FOUND));

        board.updateMainImage(mainImage);
        board.updateTitle(request.getTitle());

        Context context = board.getContext();
        context.updateIngredients(ingredients);
        context.updateDescriptions(descriptions);

        return board;

    }

    private User findByUserId(UserId userId) {
        return userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
