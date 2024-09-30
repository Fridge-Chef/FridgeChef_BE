package Fridge_Chef.team.fridge.rest;

import Fridge_Chef.team.fridge.rest.request.FridgeIngredientNameRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.fridge.service.FridgeService;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@AuthenticationPrincipal AuthenticatedUser user) {

        UserId userId = user.userId();

        fridgeService.createFridge(userId);
    }

    @GetMapping("/")
    public List<FridgeIngredientResponse> search(@AuthenticationPrincipal AuthenticatedUser user) {

        UserId userId = user.userId();

        List<FridgeIngredientResponse> response = fridgeService.getFridgeIngredientResponse(userId);
        return response;
    }

    @PostMapping("/ingredients")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody List<String> ingredientNames) {

        UserId userId = user.userId();

        fridgeService.addIngredientsToFridge(userId, ingredientNames);
    }

    @DeleteMapping("/ingredients")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody FridgeIngredientNameRequest request) {

        UserId userId = user.userId();
        String ingredientName = request.getIngredientName();

        fridgeService.deleteIngredients(userId, ingredientName);
    }

    @PutMapping("/ingredients")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody FridgeIngredientRequest request) {

        UserId userId = user.userId();

        fridgeService.updateIngredient(userId, request);
    }
}
