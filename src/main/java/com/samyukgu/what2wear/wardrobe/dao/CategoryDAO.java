// 작성자 : 김동현
package com.samyukgu.what2wear.wardrobe.dao;

import com.samyukgu.what2wear.wardrobe.model.Category;

import java.util.List;

public interface CategoryDAO {
    List<Category> findAll();
}
