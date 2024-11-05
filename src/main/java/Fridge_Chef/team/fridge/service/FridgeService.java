package Fridge_Chef.team.fridge.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import Fridge_Chef.team.fridge.domain.Storage;
import Fridge_Chef.team.fridge.repository.FridgeIngredientRepository;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientAddRequest;
import Fridge_Chef.team.fridge.rest.request.FridgeIngredientRequest;
import Fridge_Chef.team.fridge.rest.response.FridgeIngredientResponse;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.domain.IngredientCategory;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FridgeService {

    private final UserService userService;
    private final IngredientService ingredientService;

    private final FridgeIngredientRepository fridgeIngredientRepository;
    private final FridgeRepository fridgeRepository;

    @Transactional
    public void createFridge(UserId userId, List<FridgeIngredientAddRequest> fridgeCreateRequest) {
        User user = userService.findByUser(userId);

        var fridgeOptional = fridgeRepository.findByUserId(userId);

        Fridge fridge = fridgeOptional.isEmpty() ? Fridge.builder()
                .user(user)
                .fridgeIngredients(new ArrayList<>())
                .build() : fridgeOptional.get();

        if (!fridgeCreateRequest.isEmpty()) {
            for (FridgeIngredientAddRequest request : fridgeCreateRequest) {
                Ingredient ingredient = ingredientService.getOrCreate(request.getIngredientName());
                FridgeIngredient fridgeIngredient = new FridgeIngredient(fridge, ingredient, request.getStorage());
                var ins = fridge.getFridgeIngredients().stream()
                        .filter(fridges -> fridges.getIngredient().equals(ingredient))
                        .findFirst();
                if(ins.isEmpty()){
                    fridge.getFridgeIngredients().add(fridgeIngredient);
                }
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

    public void addFridgeIngredient(Fridge fridge, List<FridgeIngredientAddRequest> request) {
        for(var ingredient : request){
            addFridgeIngredient(fridge,ingredient);
        }
    }

    @Transactional
    public void deleteIngredients(UserId userId, String ingredientName) {
        Fridge fridge = getFridge(userId);
        log.info("fridge delete name " + ingredientName);

        Optional<FridgeIngredient> ingredients = fridge.getFridgeIngredients().stream()
                .filter(fridges -> fridges.getIngredient().getName().equals(ingredientName))
                .findFirst();

        log.info("fridge ins "+ fridge.getFridgeIngredients().toString());
        log.info("fridge delete isIngredient :"+ ingredients.isPresent() +","+ingredientName);

        if(ingredients.isPresent()){
            fridgeIngredientRepository.delete(ingredients.get());
            fridge.delete(ingredients.get());
            log.info("fridge-ingredient-delete : "+ ingredientName);
        }
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
