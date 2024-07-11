package ru.semenovValentine.tgBot.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.semenovValentine.tgBot.entity.*;
import ru.semenovValentine.tgBot.rest.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("rest/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/search")
    public List<Product> getProductsByCategoryIdOrName(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "name", required = false) String name) {

        if (categoryId != null && name != null) {
            List<Product> productsByCategory = productService.getProductsByCategoryId(categoryId);
            List<Product> productsByName = productService.searchProductsByName(name);

            return productService.getContains(productsByName, productsByCategory);
        } else if (categoryId != null) {
            return productService.getProductsByCategoryId(categoryId);
        } else if (name != null) {
            return productService.searchProductsByName(name);
        }

        return List.of();
    }

    @GetMapping("/popular")
    List<Product> getPopularProducts(@RequestParam("limit") int limit) {
        return productService.getTopPopularProducts(limit);
    }
}