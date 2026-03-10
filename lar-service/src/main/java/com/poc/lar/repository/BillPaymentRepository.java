package com.poc.lar.repository;

import com.poc.lar.domain.BillPayment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BillPaymentRepository extends CrudRepository<BillPayment, UUID> {

    @Query("SELECT * FROM LAR_BILL_PAYMENTS WHERE BILL_ID = :billId ORDER BY REFERENCE_MONTH DESC")
    List<BillPayment> findByBillId(@Param("billId") UUID billId);

    @Query("SELECT p.* FROM LAR_BILL_PAYMENTS p JOIN LAR_HOUSEHOLD_BILLS b ON p.BILL_ID = b.ID WHERE b.TENANT_ID = :tenantId AND p.STATUS = 'PENDING' AND p.REFERENCE_MONTH < CURDATE()")
    List<BillPayment> findOverdue(@Param("tenantId") UUID tenantId);
}
