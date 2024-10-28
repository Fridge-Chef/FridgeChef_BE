package Fridge_Chef.team.fridge.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.domain.Storage;
import Fridge_Chef.team.fridge.repository.FridgeIngredientRepository;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.fridge.rest.request.FridgeCreateRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientAddRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.domain.IngredientCategory;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FridgeService {

    private final UserService userService;
    private final IngredientService ingredientService;

    private final FridgeIngredientRepository fridgeIngredientRepository;
    private final FridgeRepository fridgeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createFridge(UserId userId, FridgeCreateRequest fridgeCreateRequest) {

        User user = userService.findByUser(userId);

        Fridge fridge = Fridge.builder()
                .user(user)
                .fridgeIngredients(new ArrayList<>())
                .build();

        if (!fridgeCreateRequest.getIngredients().isEmpty()) {
            for (FridgeIngredientAddRequest request : fridgeCreateRequest.getIngredients()) {
                Ingredient ingredient = ingredientService.getIngredient(request.getIngredientName());
                FridgeIngredient fridgeIngredient = new FridgeIngredient(fridge, ingredient, request.getStorage());
                fridge.getFridgeIngredients().add(fridgeIngredient);
            }

            fridgeIngredientRepository.saveAll(fridge.getFridgeIngredients());
        }

        fridgeRepository.save(fridge);
    }

    public Fridge getFridge(UserId userId) {
        return fridgeRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.FRIDGE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<FridgeIngredientResponse> getFridgeIngredientResponse(UserId userId) {

        Fridge fridge = getFridge(userId);

        return fridge.getFridgeIngredients().stream()
                .map(fridgeIngredient -> FridgeIngredientResponse.builder()
                        .ingredientName(fridgeIngredient.getIngredient().getName())
                        .expirationDate(fridgeIngredient.getExpirationDate())
                        .storage(fridgeIngredient.getStorage())
                        .ingredientCategory(fridgeIngredient.getIngredientCategory())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void addFridgeIngredient(Fridge fridge, FridgeIngredientAddRequest request) {

        String ingredientName = request.getIngredientName();
        Storage storage = request.getStorage();

        Ingredient ingredient = ingredientService.getIngredient(ingredientName);

        if (isExist(fridge, ingredient.getName())) {
            throw new ApiException(ErrorCode.INGREDIENT_ALREADY_EXISTS);
        }

        FridgeIngredient fridgeIngredient = createFridgeIngredient(fridge, ingredient, storage);
        addIngredientToFridge(fridge, fridgeIngredient);
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
        String category = request.getIngredientCategory();
        LocalDate exp = request.getExpirationDate();

        Fridge fridge = getFridge(userId);
        FridgeIngredient updateIngredient = getFridgeIngredient(fridge, ingredientName);

        if (category != null) {
            IngredientCategory ingredientCategory = ingredientService.getIngredientCategory(category);
            updateIngredient.updateCategory(ingredientCategory);
        }

        if (exp != null) {
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
                .ingredientCategory(IngredientCategory.UNCATEGORIZED)
                .build();
    }

    private FridgeIngredient getFridgeIngredient(Fridge fridge, String IngredientName) {
        return fridge.getFridgeIngredients().stream()
                .filter(fridgeIngredient -> fridgeIngredient.getIngredient().getName().equals(IngredientName))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.INGREDIENT_NOT_FOUND));
    }

    private void addIngredientToFridge(Fridge fridge, FridgeIngredient fridgeIngredient) {

        fridge.getFridgeIngredients().add(fridgeIngredient);
    }

    private boolean isExist(Fridge fridge, String ingredientName) {
        return fridge.getFridgeIngredients().stream()
                .anyMatch(fridgeIngredient -> fridgeIngredient.getIngredient().getName().equals(ingredientName));
    }
}
