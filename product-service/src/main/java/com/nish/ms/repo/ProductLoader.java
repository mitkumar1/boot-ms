package com.nish.ms.repo;

import com.nish.ms.model.Categories;
import com.nish.ms.model.Product;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductLoader implements CommandLineRunner {
    public final ProductRepository productRepository;

    public ProductLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadProducts();
    }


    /*
Long id;
Categories categories;
String brandName;
String description;
Timestamp dateCreated;
Timestamp lastModified;
* */

    private void loadProducts() {
        if (productRepository.count() == 0) {
            productRepository.save(
                    Product.builder()
                            .categories(Categories.MOBILE)
                            .brandName("Apple")
                            .description("Iphone 10")
                            .build()
            );
            productRepository.save(
                    Product.builder()
                            .categories(Categories.TV)
                            .brandName("Sony")
                            .description("Sony Bravia X55")
                            .build()
            );
            productRepository.save(
                    Product.builder()
                            .categories(Categories.COMPUTER_ACCESSORIES)
                            .brandName("LG")
                            .description("Mouse LG")
                            .build()
            );
            System.out.println("Sample Products Loaded");
        }
    }
}