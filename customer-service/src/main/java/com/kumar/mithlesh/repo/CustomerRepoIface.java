package com.kumar.mithlesh.repo;

import com.kumar.mithlesh.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepoIface extends JpaRepository<Customer, Integer> {

    // Find customer by email
    Optional<Customer> findByEmail(String email);

    // Find customers by name containing (case-insensitive)
    List<Customer> findByCustomerNameContainingIgnoreCase(String name);

    // Find customer by mobile number
    Optional<Customer> findByMobileNumber(Long mobileNumber);

    // Custom query to find customers by name pattern
    @Query("SELECT c FROM Customer c WHERE c.customerName LIKE %:name%")
    List<Customer> findCustomersByNamePattern(@Param("name") String name);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if mobile number exists
    boolean existsByMobileNumber(Long mobileNumber);
}
