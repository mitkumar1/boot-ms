package com.kumar.mithlesh.own.repo;

import com.kumar.mithlesh.own.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepoIface extends JpaRepository<Customer, Integer> {
}
