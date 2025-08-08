package com.kumar.mithlesh.own.controller;

import com.kumar.mithlesh.own.model.Product;
import com.kumar.mithlesh.own.service.ProductService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping({"/{productId}"})
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product product1 = productService.insert(product);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("product", "/api/v1/product/" + product1.getId().toString());
        return new ResponseEntity<>(product1, httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping({"/{productId}"})
    public ResponseEntity<Product> updateProduct(@PathVariable("productId") Long productId, @RequestBody Product product) {
        productService.updateProduct(productId, product);
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    @DeleteMapping({"/{productId}"})
    public ResponseEntity<Product> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
