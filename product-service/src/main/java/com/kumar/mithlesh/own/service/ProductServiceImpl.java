package com.kumar.mithlesh.own.service;

import com.kumar.mithlesh.own.model.Product;
import com.kumar.mithlesh.own.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        productRepository.findAll().forEach(products::add);
        return products;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }

    @Override
    public Product insert(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void updateProduct(Long id, Product product) {
        Product productFromDb = productRepository.findById(id).get();
        System.out.println(productFromDb.toString());
        productFromDb.setCategories(product.getCategories());
        productFromDb.setBrandName(product.getBrandName());
        productFromDb.setDescription(product.getDescription());
        productRepository.save(productFromDb);
    }

    @Override
    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }
}
