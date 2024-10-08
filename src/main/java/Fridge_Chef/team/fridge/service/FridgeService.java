package Fridge_Chef.team.fridge.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.domain.Storage;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientAddRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final UserService userService;
    private final IngredientService ingredientService;

    @Transactional
    public void createFridge(UserId userId) {

        User user = userService.findByUser(userId);

        Fridge fridge = Fridge.builder()
                .user(user)
                .build();

        fridgeRepository.save(fridge);

        //TODO
        // - 생성 후 재료 세팅
    }

    public Fridge getFridge(UserId userId) {
        return fridgeRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.FRIDGE_NOT_FOUND));
    }

    //냉장고 조회
    public List<FridgeIngredientResponse> getFridgeIngredientResponse(UserId userId) {

        Fridge fridge = getFridge(userId);

        List<FridgeIngredientResponse> fridgeIngredientResponses = fridge.getFridgeIngredients().stream()
                .map(fridgeIngredient -> FridgeIngredientResponse.builder()
                        .ingredientName(fridgeIngredient.getIngredient().getName())
                        .expirationDate(fridgeIngredient.getExpirationDate())
                        .storage(fridgeIngredient.getStorage())
                        .build())
                .collect(Collectors.toList());

        return fridgeIngredientResponses;
    }

    @Transactional
    public void addIngredientToFridge(UserId userId, FridgeIngredientAddRequest request) {

        String ingredientName = request.getIngredientName();
        Storage storage = request.getStorage();

        Fridge fridge = getFridge(userId);
        Ingredient ingredient = ingredientService.getIngredient(ingredientName);

        if (isExist(fridge, ingredient.getName())) {
            throw new ApiException(ErrorCode.INGREDIENT_ALREADY_EXISTS);
        }

        FridgeIngredient fridgeIngredient = createFridgeIngredient(fridge, ingredient, storage);
        fridge.getFridgeIngredients().add(fridgeIngredient);
        fridgeRepository.save(fridge);
    }

    @Transactional
    public void deleteIngredients(UserId userId, String ingredientName) {

        Fridge fridge = getFridge(userId);

        FridgeIngredient delIngredient = getFridgeIngredient(fridge, ingredientName);

        fridge.getFridgeIngredients().remove(delIngredient);
        fridgeRepository.save(fridge);
    }

    @Transactional
    public void updateIngredient(UserId userId, FridgeIngredientRequest request) {

        String ingredientName = request.getIngredientName();
        LocalDate exp = request.getExpirationDate();

        Fridge fridge = getFridge(userId);
        FridgeIngredient updateIngredient = getFridgeIngredient(fridge, ingredientName);

        if (request.getExpirationDate() != null) {
            updateIngredient.updateExpirationDate(exp);
        }

        fridgeRepository.save(fridge);
    }

    private FridgeIngredient createFridgeIngredient(Fridge fridge, Ingredient ingredient, Storage storage) {
        return FridgeIngredient.builder()
                .fridge(fridge)
                .ingredient(ingredient)
                .expirationDate(null)
                .storage(storage)
                .build();
    }

    private FridgeIngredient getFridgeIngredient(Fridge fridge, String IngredientName) {
        return fridge.getFridgeIngredients().stream()
                .filter(fridgeIngredient -> fridgeIngredient.getIngredient().getName().equals(IngredientName))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.INGREDIENT_NOT_FOUND));
    }

    private boolean isExist(Fridge fridge, String ingredientName) {
        return fridge.getFridgeIngredients().stream()
                .anyMatch(fridgeIngredient -> fridgeIngredient.getIngredient().getName().equals(ingredientName));
    }
}
