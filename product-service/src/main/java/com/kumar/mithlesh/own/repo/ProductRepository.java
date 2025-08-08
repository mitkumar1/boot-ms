package com.kumar.mithlesh.own.repo;

import com.kumar.mithlesh.own.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
}
