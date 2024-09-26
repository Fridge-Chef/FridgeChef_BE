package Fridge_Chef.team.fridge.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;
    private final IngredientService ingredientService;

    //냉장고 생성
    public void createFridge(UUID userId) {
        User user = userRepository.findByUserId_Value(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Fridge fridge = Fridge.builder()
                .user(user)
                .build();

        fridgeRepository.save(fridge);
    }

    //냉장고 조회
    public List<FridgeIngredientResponse> getFridge(UUID userId) {

        Fridge fridge = fridgeRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

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
    public void addIngredients(UUID userId, List<FridgeIngredientRequest> ingredientsRequest) {

        Fridge fridge = fridgeRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        for (FridgeIngredientRequest request : ingredientsRequest) {
            String ingredientName = request.getIngredientName();

            if (ingredientService.exist(ingredientName)) {
                Ingredient ingredient = ingredientService.findIngredientByName(ingredientName);

                FridgeIngredient fridgeIngredient = FridgeIngredient.builder()
                        .fridge(fridge)
                        .ingredient(ingredient)
                        .expirationDate(request.getExpirationDate())
                        .build();
            } else {
                Ingredient ingredient = Ingredient.builder().build();
            }
        }
    }

    //냉장고 재료 삭제
    public void deleteIngredients(Long userId, FridgeIngredient ingredientRequest) {
    }

    //냉장고 재료 수정
}
