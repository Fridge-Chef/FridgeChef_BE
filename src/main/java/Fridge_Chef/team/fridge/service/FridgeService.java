package Fridge_Chef.team.fridge.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.domain.Storage;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final IngredientService ingredientService;

    @Transactional
    public void createFridge(UserId userId) {

        User user = userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Fridge fridge = Fridge.builder()
                .user(user)
                .build();

        fridgeRepository.save(fridge);
    }

    public FridgeIngredient createFridgeIngredient(Fridge fridge, Ingredient ingredient) {
        return FridgeIngredient.builder()
                .fridge(fridge)
                .ingredient(ingredient)
                .expirationDate(null)
                .storage(Storage.REFRIGERATION)
                .build();
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

    //냉장고 재료 등록
    @Transactional
    public void addIngredientsToFridge(UserId userId, List<String> ingredientNames) {

        Fridge fridge = getFridge(userId);

        for (String ingredientName : ingredientNames) {

            if (!ingredientService.exist(ingredientName)) {
                throw new ApiException(ErrorCode.INGREDIENT_NOT_FOUND);
            }

            Ingredient ingredient = ingredientService.createIngredient(ingredientName);
            ingredientService.insertIngredient(ingredient);

            FridgeIngredient fridgeIngredient = createFridgeIngredient(fridge, ingredient);
            fridge.getFridgeIngredients().add(fridgeIngredient);
        }

        fridgeRepository.save(fridge);
    }

    //냉장고 재료 삭제
    @Transactional
    public void deleteIngredients(UserId userId, String ingredientName) {

        Fridge fridge = getFridge(userId);

        FridgeIngredient delIngredient = getFridgeIngredient(fridge, ingredientName);

        fridge.getFridgeIngredients().remove(delIngredient);
        fridgeRepository.save(fridge);
    }

    //냉장고 재료 수정
    @Transactional
    public void updateIngredient(UserId userId, FridgeIngredientRequest request) {

        Fridge fridge = getFridge(userId);

        FridgeIngredient updateIngredient = getFridgeIngredient(fridge, request.getIngredientName());

        if (request.getExpirationDate() != null) {
            updateIngredient.updateExpirationDate(request.getExpirationDate());
        }

        if (request.getStorage() != null) {
            updateIngredient.updateStorage(request.getStorage());
        }

        fridgeRepository.save(fridge);
    }

    private FridgeIngredient getFridgeIngredient(Fridge fridge, String IngredientName) {
        return fridge.getFridgeIngredients().stream()
                .filter(fridgeIngredient -> fridgeIngredient.getIngredient().getName().equals(IngredientName))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.INGREDIENT_NOT_FOUND));
    }
}
