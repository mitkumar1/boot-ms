package com.kumar.mithlesh.own.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "customer")
public class Customer {
    @Id
    int customerId;
    @Column(name = "customer_name", nullable = false)
    String customerName;
    @Column
    String email;
    @Column(name = "mobile_number")
    long mobileNumber;
}
