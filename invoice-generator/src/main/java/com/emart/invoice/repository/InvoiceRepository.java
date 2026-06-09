package com.emart.invoice.repository;

import com.emart.invoice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    List<Invoice> findByOrderId(Integer orderId);
    List<Invoice> findByGeneratedByUserId(Integer userId);
}
