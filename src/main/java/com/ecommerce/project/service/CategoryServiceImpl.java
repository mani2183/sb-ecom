package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class CategoryServiceImpl implements  CategoryService{

    private List<Category> categories = new ArrayList<>();
    private Long Id = 1L;


    @Override
    public List<Category> getAllCategories() {
        return categories;
    }

    @Override
    public void createCategoty(Category category) {
        category.setCategoryId(Id++);
        categories.add(category);

    }

    @Override
    public String deleteCategory(Long categoryId) {
        Category category = categories.stream()
                .filter(c-> c.getCategoryId().equals(categoryId))
                .findFirst().orElse(null);
        if(category == null){
            return "category id not found";
        }
        categories.remove(category);
        return "category deleted";
    }
}
