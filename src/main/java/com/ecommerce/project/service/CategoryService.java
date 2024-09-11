package com.ecommerce.project.service;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

public interface CategoryService {
    //Bütün kategoriler gelsin
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    //Yeni kategori
    CategoryDTO createCategory(CategoryDTO categoryDTO);

    //Kategoriyi sil
    CategoryDTO deleteCategory(Long categoryId);

    //Kategoriyi güncelle
    CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId);
}
