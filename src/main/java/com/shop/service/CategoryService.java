package com.shop.service;

import com.shop.domain.Category;
import com.shop.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<Category> findAll() {
    return categoryRepository.findAll();
  }
}
