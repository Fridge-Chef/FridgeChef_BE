package Fridge_Chef.team.category.initializer;

import Fridge_Chef.team.category.service.CategoryService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CategoryInitializer {
    private final CategoryService categoryService;

    @PostConstruct
    public void initCategories() {
        List<String> defaultCategories = List.of("레시피", "사용자 레시피");
        defaultCategories.forEach(this::addCategoryIfNotExists);
    }

    private void addCategoryIfNotExists(String categoryName) {
        if (!categoryService.existsByName(categoryName)) {
            categoryService.addCategory(categoryName);
        }
    }
}
