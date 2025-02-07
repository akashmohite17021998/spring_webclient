package com.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.model.Product;
import com.example.model.ProductCreateRessponse;

@Service
public class ProductService {

    List<Product> products;

    public ProductService() {
        this.products = new ArrayList<>();
    }

    public ProductCreateRessponse createProduct(Product product) {
        if(product.getName() == null){
            throw new RuntimeException("name can't be null");
        }
        product.setId(products.size()+1);
        products.add(product);
        ProductCreateRessponse ressponse = new ProductCreateRessponse();
        ressponse.setMessage("SUCCESS");
        ressponse.setStatusCode("200");
        return ressponse;
    }

    public Product getProductById(int id) {
        return products.stream().filter(product -> product.getId() == id).findFirst().orElseThrow();
    }

    public List<Product> getProductsByName(String name) {
        return products.stream().filter(product -> product.getName().equalsIgnoreCase(name)).collect(Collectors.toList());
    }

    public String deleteProduct(int id) {
        try {
            Thread.sleep(800);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        products.remove(getProductById(id));
        return "Product deleted";
    }

    public String updateProduct(Product product) {

        Product existingProd = getProductById(product.getId());
        if(existingProd == null){
            throw new RuntimeException("Product does not exist");
        }
        products.set(products.indexOf(existingProd), product);
        return "Product Updated..";
    }
}
