package Fridge_Chef.team.board.rest;


import Fridge_Chef.team.board.rest.request.BoardPostCreateRequest;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.board.service.request.BoardCreateRequest;
import Fridge_Chef.team.board.service.request.BoardIngredientRequest;
import Fridge_Chef.team.category.domain.Category;
import Fridge_Chef.team.category.service.CategoryService;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.service.RecipeService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final ImageService imageService;
    private final CategoryService categoryService;
    private final RecipeService recipeService;
    private final IngredientService ingredientService;

    @PostMapping
    void create(@AuthenticationPrincipal AuthenticatedUser user,
                @RequestBody BoardPostCreateRequest request) {
        Image mainImage = imageService.imageUpload(user.userId(), request.getMainImage());
        List<Image> images = imageService.imageUploads(user.userId(), request.getStepImages());
        Category category = categoryService.findById(request.getCategoryId());

        List<BoardIngredientRequest> ingredients = new ArrayList<>();

        BoardCreateRequest createRequest = request.toCreate(mainImage, images, ingredients, category);

        boardService.create(user.userId(), createRequest);
    }
}
