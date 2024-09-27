package Fridge_Chef.team.fridge.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientDeleteRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final IngredientService ingredientService;

    //냉장고 생성
    public void createFridge(UserId userId) {

        //UserId를 통해서 user찾기 해야됨
        User user = userRepository.findByUserId_Value(userId.getValue())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Fridge fridge = Fridge.builder()
                .user(user)
                .build();

        fridgeRepository.save(fridge);
    }

    public FridgeIngredient createFridgeIngredient(Fridge fridge, Ingredient ingredient, LocalDate expirationDate) {
        return FridgeIngredient.builder()
                .fridge(fridge)
                .ingredient(ingredient)
                .expirationDate(expirationDate)
                .build();
    }

    public Fridge getFridge(UserId userId) {
        return fridgeRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    //냉장고 조회
    public List<FridgeIngredientResponse> getFridgeIngredientResponse(UserId userId) {

        Fridge fridge = getFridge(userId);

        List<FridgeIngredientResponse> fridgeIngredientResponses = fridge.getFridgeIngredients().stream()
                .map(fridgeIngredient -> FridgeIngredientResponse.builder()
                        .ingredientName(fridgeIngredient.getIngredient().getName())
                        .isSeasoning(fridgeIngredient.getIngredient().getIsSeasoning())
                        .expirationDate(fridgeIngredient.getExpirationDate())
                        .build())
                .collect(Collectors.toList());

        return fridgeIngredientResponses;
    }

    //냉장고 재료 등록
    public void addIngredientsToFridge(UserId userId, List<FridgeIngredientRequest> ingredientsRequest) {

        Fridge fridge = getFridge(userId);

        for (FridgeIngredientRequest request : ingredientsRequest) {
            String ingredientName = request.getIngredientName();

            if (ingredientService.exist(ingredientName)) {
                Ingredient ingredient = ingredientService.getIngredient(ingredientName);
                FridgeIngredient fridgeIngredient = createFridgeIngredient(fridge, ingredient, request.getExpirationDate());
                fridge.getFridgeIngredients().add(fridgeIngredient);
            } else {
                Ingredient ingredient = ingredientService.createIngredient(ingredientName);
                ingredientService.insertIngredient(ingredient);

                FridgeIngredient fridgeIngredient = createFridgeIngredient(fridge, ingredient, request.getExpirationDate());
                fridge.getFridgeIngredients().add(fridgeIngredient);
            }
        }
        fridgeRepository.save(fridge);
    }

    //냉장고 재료 삭제
    public void deleteIngredients(UserId userId, FridgeIngredientDeleteRequest request) {

        Fridge fridge = getFridge(userId);

        FridgeIngredient delIngredient = getFridgeIngredient(fridge, request.getIngredientName());

        fridge.getFridgeIngredients().remove(delIngredient);
        fridgeRepository.save(fridge);
    }

    //냉장고 재료 수정
    public void updateIngredientExpirationDate(UserId userId, FridgeIngredientRequest request) {

        Fridge fridge = getFridge(userId);

        FridgeIngredient updateIngredient = getFridgeIngredient(fridge, request.getIngredientName());

        updateIngredient.updateExpirationDate(request.getExpirationDate());
        fridgeRepository.save(fridge);
    }

    private FridgeIngredient getFridgeIngredient(Fridge fridge, String IngredientName) {
        return fridge.getFridgeIngredients().stream()
                .filter(fridgeIngredient -> fridgeIngredient.getIngredient().getName().equals(IngredientName))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
