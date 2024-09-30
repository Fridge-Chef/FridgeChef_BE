package Fridge_Chef.team.fridge.rest;

import Fridge_Chef.team.fridge.rest.request.FridgeIngredientNameRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.fridge.service.FridgeService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fridges")
@RequiredArgsConstructor
public class FridgeController {

    private final FridgeService fridgeService;

    @PostMapping("/")
    public ResponseEntity create(@AuthenticationPrincipal AuthenticatedUser user) {

        fridgeService.createFridge(user.userId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/")
    public ResponseEntity<List<FridgeIngredientResponse>> search(@AuthenticationPrincipal AuthenticatedUser user) {

        List<FridgeIngredientResponse> response = fridgeService.getFridgeIngredientResponse(user.userId());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/ingredients")
    public ResponseEntity add(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody List<String> ingredientNames) {

        fridgeService.addIngredientsToFridge(user.userId(), ingredientNames);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/ingredients")
    public ResponseEntity delete(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody FridgeIngredientNameRequest request) {

        fridgeService.deleteIngredients(user.userId(), request.getIngredientName());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/ingredients")
    public ResponseEntity update(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody FridgeIngredientRequest request) {

        fridgeService.updateIngredient(user.userId(), request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
