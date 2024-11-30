package com.ecommerce.project.repositories;

import com.ecommerce.project.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Category findByCategoryName(@NotBlank(message = "Empty category name") @Size(min = 5, message = "Category name must contain at least 5 characters") String categoryName);
    Category findByCategoryName(String categoryName);
}
