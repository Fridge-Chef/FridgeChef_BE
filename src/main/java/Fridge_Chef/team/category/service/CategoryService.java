package Fridge_Chef.team.category.service;

import Fridge_Chef.team.category.domain.Category;
import Fridge_Chef.team.category.repository.CategoryRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category findById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ApiException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category addCategory(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ApiException(ErrorCode.CATEGORY_ALREADY);
        }
        Category category = new Category(name);
        return categoryRepository.save(category);
    }

    public boolean existsByName(String categoryName) {
        return categoryRepository.existsByName(categoryName);
    }
}
