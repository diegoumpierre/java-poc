package com.poc.lar.repository;

import com.poc.lar.domain.HealthAppointment;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthAppointmentRepository extends CrudRepository<HealthAppointment, UUID> {

    @Query("SELECT * FROM LAR_HEALTH_APPOINTMENTS WHERE TENANT_ID = :tenantId ORDER BY APPOINTMENT_DATE DESC")
    List<HealthAppointment> findByTenantId(@Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_HEALTH_APPOINTMENTS WHERE ID = :id AND TENANT_ID = :tenantId")
    Optional<HealthAppointment> findByIdAndTenantId(@Param("id") UUID id, @Param("tenantId") UUID tenantId);

    @Query("SELECT * FROM LAR_HEALTH_APPOINTMENTS WHERE TENANT_ID = :tenantId AND MEMBER_ID = :memberId ORDER BY APPOINTMENT_DATE DESC")
    List<HealthAppointment> findByMemberId(@Param("tenantId") UUID tenantId, @Param("memberId") UUID memberId);

    @Query("SELECT * FROM LAR_HEALTH_APPOINTMENTS WHERE TENANT_ID = :tenantId AND APPOINTMENT_DATE >= CURDATE() AND STATUS = 'SCHEDULED' ORDER BY APPOINTMENT_DATE")
    List<HealthAppointment> findUpcoming(@Param("tenantId") UUID tenantId);
}
