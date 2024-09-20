package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.*;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.PostRepository;
import Fridge_Chef.team.board.service.request.BoardCreateRequest;
import Fridge_Chef.team.category.domain.Category;
import Fridge_Chef.team.category.repository.CategoryRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;
    private final RecipeRepository recipeRepository;


    // 게시글 작성
    public void create(UserId userId, BoardCreateRequest request) {
        Category category = findByCategory(request.getCategoryId());

        List<RecipeIngredient> boardIngredients = new ArrayList<>();
        List<Description> descriptions = new ArrayList<>();

        Context context = Context.toMyUserRecipe(boardIngredients, descriptions);
        Board board = new Board(request.getTitle(), category, context, request.getMainImage(), BoardType.USER);
        Post post = new Post(boardRepository.save(board), userId);

        postRepository.save(post);
    }

    // 게시글 수정

    // 게시글 삭제

    private Category findByCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));

    }
}
