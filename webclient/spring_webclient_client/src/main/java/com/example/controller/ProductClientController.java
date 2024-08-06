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
import com.example.model.ProductClientCreateRessponse;
import com.example.service.ProductClientService;

@RestController
@RequestMapping("/client/product")
public class ProductClientController {
    private ProductClientService productClientService;

    public ProductClientController(ProductClientService productClientService) {
        this.productClientService = productClientService;
    }

    @PostMapping("")
    ProductClientCreateRessponse createProduct(@RequestBody Product product){
        return productClientService.createProduct(product);
    }
    @PutMapping("")
    String updateProduct(@RequestBody Product product){
        return productClientService.updateProduct(product);
    }

    @GetMapping("/{id}")
    Product getProductById(@PathVariable int id){
        return productClientService.getProductById(id);
    }

    //http:/localhost:8080/product?name='java'
    @GetMapping("/")
    List<Product> getProductById(@RequestParam String name){
        return productClientService.getProductsByName(name);
    }
    @DeleteMapping("/{id}")
    String deleteProduct(@PathVariable int id){
        return productClientService.deleteProduct(id);
    }

}
