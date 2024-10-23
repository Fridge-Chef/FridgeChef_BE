package Fridge_Chef.team.recipe.rest.request;

import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecipePageRequest {

    private int page;
    private int size;
    private RecipeSearchSortType sortType;

    public PageRequest getPageRequest() {
        return PageRequest.of(page, size);  // PageRequest 생성
    }

    public RecipeSearchSortType getSortType() {
        return sortType;
    }
}
