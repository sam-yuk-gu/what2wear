package com.samyukgu.what2wear.wardrobe.service;

import com.samyukgu.what2wear.wardrobe.dao.CategoryDAO;
import com.samyukgu.what2wear.wardrobe.model.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDAO categoryDAO;

    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
}
