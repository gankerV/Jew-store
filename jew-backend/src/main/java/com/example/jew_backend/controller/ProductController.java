package com.example.jew_backend.controller;

import com.example.jew_backend.model.Product;
import com.example.jew_backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getProducts() {
        System.out.print(3);
        return productService.getAllProducts();
    }
}
