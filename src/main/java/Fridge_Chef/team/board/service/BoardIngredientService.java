package Fridge_Chef.team.board.service;

import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import Fridge_Chef.team.board.service.request.RecipeIngredientDto;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
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
    public List<RecipeIngredient> findOrCreate(List<RecipeIngredientDto> request) {
        return request.stream()
                .map(this::findOrSaveIngredient)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecipeIngredient findOrCreate(RecipeIngredientDto request) {
        return findOrSaveIngredient(request);
    }


    @Transactional
    public List<Description> uploadInstructionImages(UserId userId, BoardByRecipeRequest request) {
        if (request.getDescriptions() == null) {
            return List.of();
        }
        return request.getDescriptions()
                .stream().map(instruction -> {
                    if (instruction.getImage() == null) {
                        return descriptionRepository.save(new Description(instruction.getContent(), null));
                    }
                    Image instructionImage = imageService.imageUpload(userId, instruction.getImage());
                    return descriptionRepository.save(new Description(instruction.getContent(), instructionImage));
                }).collect(Collectors.toList());
    }

    @Transactional
    public List<Description> uploadInstructionImages(UserId userId, BoardByRecipeUpdateRequest request) {
        if (request.getInstructions() == null) {
            return List.of();
        }

        return request.getInstructions()
                .stream().map(instruction -> {
                    if (instruction.isImageChange() && instruction.getImage() == null) {
                        return new Description(instruction.getContent(), null);
                    }
                    Image instructionImage = imageService.imageUpload(userId, instruction.getImage());
                    return new Description(instruction.getContent(), instructionImage);
                }).collect(Collectors.toList());
    }


    private RecipeIngredient findOrSaveIngredient(String name, String details) {
        Ingredient ingredient = updateRecipeIngredient(name);
        RecipeIngredient findRecipeIngredient = RecipeIngredient.ofMyRecipe(ingredient, details);
        return recipeIngredientRepository.save(findRecipeIngredient);
    }


    private RecipeIngredient findOrSaveIngredient(RecipeIngredientDto dto) {
        return findOrSaveIngredient(dto.name(), dto.detail());
    }

    private RecipeIngredient findOrSaveIngredient(BoardByRecipeRequest.RecipeIngredient recipeIngredient) {
        return findOrSaveIngredient(recipeIngredient.getName(), recipeIngredient.getDetails());
    }

    private RecipeIngredient findOrSaveIngredient(BoardByRecipeUpdateRequest.RecipeIngredient recipeIngredient) {
        return findOrSaveIngredient(recipeIngredient.getName(), recipeIngredient.getDetails());
    }

    private Ingredient updateRecipeIngredient(String name) {
        return ingredientRepository.findByName(name)
                .orElseGet(() -> saveNewIngredient(name));
    }

    private Ingredient saveNewIngredient(String ingredientName) {
        return ingredientRepository.save(new Ingredient(ingredientName));
    }
}
