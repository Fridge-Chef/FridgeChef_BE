package Fridge_Chef.team.recipe.rest.request;

import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;

public record RecipePageRequest(int page, int size, RecipeSearchSortType sortType) {
}
