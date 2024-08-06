package com.example.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Product;
import com.example.model.ProductCreateRessponse;
import com.example.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("")
    ProductCreateRessponse createProduct(@RequestBody Product product){
        return productService.createProduct(product);
    }
    @PutMapping("")
    String updateProduct(@RequestBody Product product){
        return productService.updateProduct(product);
    }

    @GetMapping("/{id}")
    Product getProductById(@PathVariable int id){
        return productService.getProductById(id);
    }

    //http:/localhost:8080/product?name='java'
    @GetMapping("/")
    List<Product> getProductById(@RequestParam String name){
        return productService.getProductsByName(name);
    }
    @DeleteMapping("/{id}")
    String deleteProduct(@PathVariable int id){
        return productService.deleteProduct(id);
    }

}
