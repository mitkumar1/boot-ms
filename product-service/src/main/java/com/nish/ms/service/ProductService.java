package com.nish.ms.service;

import com.nish.ms.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> getProducts();

    Product getProductById(Long id);

    Product insert(Product product);

    void updateProduct(Long id, Product product);

    void deleteProduct(Long todoId);
}