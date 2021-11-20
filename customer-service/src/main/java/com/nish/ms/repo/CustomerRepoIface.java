package com.nish.ms.repo;

import com.nish.ms.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepoIface extends JpaRepository<Customer, Integer> {

}
