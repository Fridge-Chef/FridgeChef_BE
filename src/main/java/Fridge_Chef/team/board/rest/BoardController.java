package Fridge_Chef.team.board.rest;


import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.rest.request.BoardByRecipeDeleteRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.service.BoardIngredientService;
import Fridge_Chef.team.board.service.BoardRecipeService;
import Fridge_Chef.team.board.service.BoardService;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final BoardRecipeService boardRecipeService;
    private final BoardIngredientService boardIngredientService;
    private final ImageService imageService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @ModelAttribute BoardByRecipeRequest request
    ) {
        boardService.textFilterPolicy(request);

        Image image = imageService.imageUpload(user.userId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.userId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        boardRecipeService.create(user.userId(), request, ingredients, descriptions, image);
    }

    @PutMapping
    void update(@AuthenticationPrincipal AuthenticatedUser user,
                @Valid @ModelAttribute BoardByRecipeUpdateRequest request) {

        Image mainImage = imageService.uploadImageWithId(user.userId(), request.isMainImageChange(),
                request.getMainImageId(), request.getMainImage());

        List<Description> descriptions = boardIngredientService.uploadInstructionImages(user.userId(), request);
        List<RecipeIngredient> ingredients = boardIngredientService.findOrCreate(request);

        boardRecipeService.update(user.userId(), request, ingredients, descriptions, mainImage);
    }

    @DeleteMapping
    void delete(@AuthenticationPrincipal AuthenticatedUser user,
                @RequestBody BoardByRecipeDeleteRequest request) {
        boardService.delete(user.userId(), request.id());
    }
}
