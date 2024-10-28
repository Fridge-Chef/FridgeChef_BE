package Fridge_Chef.team.ingredient.rest;

import Fridge_Chef.team.ingredient.rest.response.IngredientSearchResponse;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping("/search")
    public IngredientSearchResponse search(@AuthenticationPrincipal AuthenticatedUser user, @RequestParam String keyword) {

        IngredientSearchResponse response = ingredientService.searchIngredients(keyword);

        return response;
    }
}
