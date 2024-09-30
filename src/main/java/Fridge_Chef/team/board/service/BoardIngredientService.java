package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardIngredientService {
    private final ImageService imageService;
    private final IngredientRepository ingredientRepository;
    private final DescriptionRepository descriptionRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    @Transactional
    public List<RecipeIngredient> findOrCreate(BoardByRecipeRequest request) {
        return request.getRecipeIngredients().stream()
                .map(this::findOrSaveIngredient)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<RecipeIngredient> findOrCreate(BoardByRecipeUpdateRequest request) {
        return request.getRecipeIngredients().stream()
                .map(this::findOrSaveIngredient)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Description> uploadInstructionImages(UserId userId, BoardByRecipeRequest request) {
        return request.getRecipeIngredients()
                .stream().map(instruction -> {
                    Image instructionImage = imageService.imageUpload(userId, request.getMainImage());
                    Description description = descriptionRepository.save(new Description(instruction.getDetails(), instructionImage));
                    return descriptionRepository.save(description);
                }).collect(Collectors.toList());
    }

    @Transactional
    public List<Description> uploadInstructionImages(UserId userId, BoardByRecipeUpdateRequest request) {
        return request.getRecipeIngredients()
                .stream().map(instruction -> {
                    Image instructionImage = imageService.uploadImageWithId(userId, request.isMainImageChange(), request.getMainImageId(), request.getMainImage());
                    Description description = descriptionRepository.findById(instruction.getId())
                            .orElse(new Description(instruction.getDetails(), instructionImage))
                            .update(instruction.getDetails(), instructionImage);
                    return descriptionRepository.save(description);
                }).collect(Collectors.toList());
    }

    private RecipeIngredient findOrSaveIngredient(BoardByRecipeRequest.RecipeIngredient recipeIngredient) {
        Ingredient ingredient = updateRecipeIngredient(recipeIngredient.getName());
        RecipeIngredient findRecipeIngredient = RecipeIngredient.ofMyRecipe(ingredient, recipeIngredient.getDetails());
        return recipeIngredientRepository.save(findRecipeIngredient);
    }

    private RecipeIngredient findOrSaveIngredient(BoardByRecipeUpdateRequest.RecipeIngredient recipeIngredient) {
        Ingredient ingredient = updateRecipeIngredient(recipeIngredient.getName());
        RecipeIngredient findRecipeIngredient = updateRecipeDetails(recipeIngredient.getId(), ingredient, recipeIngredient.getDetails());
        return recipeIngredientRepository.save(findRecipeIngredient);
    }

    private Ingredient updateRecipeIngredient(String name) {
        return ingredientRepository.findByName(name)
                .orElseGet(() -> saveNewIngredient(name));
    }

    private RecipeIngredient updateRecipeDetails(Long id, Ingredient ingredient, String details) {
        return recipeIngredientRepository.findById(id)
                .orElse(RecipeIngredient.ofMyRecipe(ingredient, details))
                .update(ingredient, details);
    }

    private Ingredient saveNewIngredient(String ingredientName) {
        return ingredientRepository.save(new Ingredient(ingredientName));
    }

//    private boolean isSeasoning(String ingredientName) {
//        return IngredientService.SEASONINGS.stream().anyMatch(ingredientName::contains);
//    }
}
