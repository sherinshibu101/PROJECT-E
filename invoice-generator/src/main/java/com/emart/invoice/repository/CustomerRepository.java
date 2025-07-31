package com.emart.invoice.repository;

import com.emart.invoice.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    // JPA provides these methods automatically:
    // save(), findById(), findAll(), delete(), count()
    
    // Custom query methods (JPA creates these automatically from method names):
    Customer findByEmail(String email);
    Customer findByName(String name);

    // Reset AUTO_INCREMENT to start from 1
    @Modifying
    @Transactional
    @Query(value = "ALTER SEQUENCE customers_id_seq RESTART WITH 1", nativeQuery = true)
    void resetAutoIncrement();
}