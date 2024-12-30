package Fridge_Chef.team.ingredient.service;

import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.rest.response.IngredientSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    @Transactional(readOnly = true)
    public IngredientSearchResponse findAllIngredients() {
        return new IngredientSearchResponse(
                ingredientRepository.findAll().stream()
                        .map(Ingredient::getName)
                        .toList()
        );
    }

    @Transactional
    public Ingredient getIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
    }
}
