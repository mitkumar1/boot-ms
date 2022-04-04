package com.nish.ms.controller;

import com.nish.ms.model.Product;
import com.nish.ms.service.ProductService;
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

    //The function receives a GET request, processes it and gives back a list of Product as a response.
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    //The function receives a GET request, processes it, and gives back a list of Product as a response.
    @GetMapping({"/{productId}"})
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }

    //The function receives a POST request, processes it, creates a new Product and saves it to the database, and returns a resource link to the created product.
    @PostMapping
    public ResponseEntity<Product> saveProduct(@RequestBody Product product) {
        Product product1 = productService.insert(product);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("product", "/api/v1/product/" + product1.getId().toString());
        return new ResponseEntity<>(product1, httpHeaders, HttpStatus.CREATED);
    }
    //The function receives a PUT request, updates the Product with the specified Id and returns the updated Product
    @PutMapping({"/{productId}"})
    public ResponseEntity<Product> updateProduct(@PathVariable("productId") Long productId, @RequestBody Product product) {
        productService.updateProduct(productId, product);
        return new ResponseEntity<>(productService.getProductById(productId), HttpStatus.OK);
    }
    //The function receives a DELETE request, deletes the Product with the specified Id.
    @DeleteMapping({"/{productId}"})
    public ResponseEntity<Product> deleteProduct(@PathVariable("productId") Long productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
