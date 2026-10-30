package com.shop.controller;

import com.shop.dto.PageMetaDto;
import com.shop.dto.ProductDto;
import com.shop.dto.ProductPageDto;
import com.shop.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService productService;

  public ProductController(ProductService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ProductPageDto list(
      @RequestParam(required = false) String category,
      @RequestParam(name = "q", required = false) String search,
      @RequestParam(required = false) Integer priceMin,
      @RequestParam(required = false) Integer priceMax,
      @RequestParam(required = false) Boolean available,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "6") int perPage) {
    var result =
        productService.findPage(category, search, priceMin, priceMax, available, page, perPage);
    var items = result.items().stream().map(ProductDto::from).toList();
    var totalPages = (int) Math.ceil((double) result.total() / perPage);
    var meta = new PageMetaDto(page, perPage, result.total(), totalPages);
    return new ProductPageDto(items, meta);
  }

  @GetMapping("/{id}")
  public ProductDto getById(@PathVariable int id) {
    return ProductDto.from(productService.findById(id));
  }
}
