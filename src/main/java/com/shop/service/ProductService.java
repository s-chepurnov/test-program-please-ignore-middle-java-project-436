package com.shop.service;

import com.shop.domain.Product;
import com.shop.exception.NotFoundException;
import com.shop.repository.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductRepository.Page findPage(String category, String search, Integer priceMin,
                                           Integer priceMax, Boolean available,
                                           int page, int perPage) {
        return productRepository.findPage(category, search, priceMin, priceMax, available, page, perPage);
    }

    public Product findById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }
}
