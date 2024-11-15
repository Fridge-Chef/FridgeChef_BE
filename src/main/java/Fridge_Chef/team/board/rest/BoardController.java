package Fridge_Chef.team.board.rest;


import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.service.BoardRecipeService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardController {
    private final BoardRecipeService boardRecipeService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    void create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @ModelAttribute BoardByRecipeRequest request
    ) {
        boardRecipeService.create(user.userId(), request);
    }

    @PutMapping
    void update(@AuthenticationPrincipal AuthenticatedUser user,
                @Valid @ModelAttribute BoardByRecipeUpdateRequest request) {
        boardRecipeService.update(user.userId(), request);
    }
}
