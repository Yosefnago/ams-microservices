package com.ams.accountantUser.repository;

import com.ams.accountantUser.entity.InvoiceIncomes;
import com.ams.dtos.invoiceDto.InvoiceIncomeNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceIncomeRepository extends JpaRepository<InvoiceIncomes, Long> {

    @Query("SELECT new com.ams.dtos.invoiceDto.InvoiceIncomeNumber(i.ownerId, SUM(i.total_price_after_vat)) " +
            "FROM InvoiceIncomes i where i.ownerId = :clientId GROUP BY i.ownerId")
    InvoiceIncomeNumber getIncomeDto(@Param("clientId") String clientId);

}
