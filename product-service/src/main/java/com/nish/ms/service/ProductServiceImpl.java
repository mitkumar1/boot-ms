package com.nish.ms.service;

import com.nish.ms.model.Product;
import com.nish.ms.repo.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    ProductRepository ProductRepository;

    public ProductServiceImpl(ProductRepository ProductRepository) {
        this.ProductRepository = ProductRepository;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        ProductRepository.findAll().forEach(products::add);
        return products;
    }

    @Override
    public Product getProductById(Long id) {
        return ProductRepository.findById(id).get();
    }

    @Override
    public Product insert(Product Product) {
        return ProductRepository.save(Product);
    }

    @Override
    public void updateProduct(Long id, Product product) {
        Product productFromDb = ProductRepository.findById(id).get();
        System.out.println(productFromDb.toString());
        productFromDb.setCategories(product.getCategories());
        productFromDb.setBrandName(product.getBrandName());
        productFromDb.setDescription(product.getDescription());
        ProductRepository.save(productFromDb);
    }

    @Override
    public void deleteProduct(Long ProductId) {
        ProductRepository.deleteById(ProductId);
    }
}
