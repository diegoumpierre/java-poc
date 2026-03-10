package com.poc.lar.service;

import com.poc.lar.converter.OutingConverter;
import com.poc.lar.domain.FamilyMember;
import com.poc.lar.domain.OutingRequest;
import com.poc.lar.dto.OutingApprovalRequest;
import com.poc.lar.dto.OutingDTO;
import com.poc.lar.dto.OutingRejectionRequest;
import com.poc.lar.dto.OutingRequestRequest;
import com.poc.lar.repository.ChecklistResponseRepository;
import com.poc.lar.repository.FamilyMemberRepository;
import com.poc.lar.repository.OutingRequestRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutingService {

    private final OutingRequestRepository outingRequestRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final OutingConverter outingConverter;
    private final ChecklistResponseRepository checklistResponseRepository;

    public List<OutingDTO> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all outings for tenant: {}", tenantId);
        return outingConverter.toDTOList(outingRequestRepository.findByTenantId(tenantId));
    }

    public Optional<OutingDTO> findById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding outing by id: {} for tenant: {}", id, tenantId);
        return outingRequestRepository.findByIdAndTenantId(id, tenantId)
                .map(outing -> {
                    String nickname = familyMemberRepository.findByIdAndTenantId(outing.getMemberId(), tenantId)
                            .map(FamilyMember::getNickname)
                            .orElse(null);
                    return outingConverter.toDTO(outing, nickname);
                });
    }

    public List<OutingDTO> findPending() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding pending outings for tenant: {}", tenantId);
        return outingConverter.toDTOList(outingRequestRepository.findPending(tenantId));
    }

    public List<OutingDTO> findActive() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding active outings for tenant: {}", tenantId);
        return outingConverter.toDTOList(outingRequestRepository.findActive(tenantId));
    }

    public List<OutingDTO> findByMember(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding outings for member: {} in tenant: {}", memberId, tenantId);
        return outingConverter.toDTOList(outingRequestRepository.findByMemberId(tenantId, memberId));
    }

    public OutingDTO create(OutingRequestRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Creating outing request for tenant: {}, user: {}", tenantId, userId);

        // Find the member associated with the current user
        FamilyMember member = familyMemberRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new RuntimeException("Family member not found for user: " + userId));

        OutingRequest outing = OutingRequest.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .memberId(member.getId())
                .eventName(req.eventName())
                .eventDate(req.eventDate())
                .eventTime(req.eventTime())
                .address(req.address())
                .addressLat(req.addressLat())
                .addressLng(req.addressLng())
                .locationContactName(req.locationContactName())
                .locationContactPhone(req.locationContactPhone())
                .departureTime(req.departureTime())
                .returnMethod(req.returnMethod())
                .returnMethodDetail(req.returnMethodDetail())
                .estimatedReturnTime(req.estimatedReturnTime())
                .companions(outingConverter.serializeCompanions(req.companions()))
                .status("PENDING")
                .teenNotes(req.teenNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return outingConverter.toDTO(outingRequestRepository.save(outing), member.getNickname());
    }

    public OutingDTO update(UUID id, OutingRequestRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating outing: {} for tenant: {}", id, tenantId);

        OutingRequest outing = outingRequestRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Outing not found: " + id));

        outing.setEventName(req.eventName());
        outing.setEventDate(req.eventDate());
        outing.setEventTime(req.eventTime());
        outing.setAddress(req.address());
        outing.setAddressLat(req.addressLat());
        outing.setAddressLng(req.addressLng());
        outing.setLocationContactName(req.locationContactName());
        outing.setLocationContactPhone(req.locationContactPhone());
        outing.setDepartureTime(req.departureTime());
        outing.setReturnMethod(req.returnMethod());
        outing.setReturnMethodDetail(req.returnMethodDetail());
        outing.setEstimatedReturnTime(req.estimatedReturnTime());
        outing.setCompanions(outingConverter.serializeCompanions(req.companions()));
        outing.setTeenNotes(req.teenNotes());
        outing.setUpdatedAt(LocalDateTime.now());
        outing.markAsExisting();

        return outingConverter.toDTO(outingRequestRepository.save(outing));
    }

    public OutingDTO approve(UUID id, OutingApprovalRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Approving outing: {} by user: {}", id, userId);

        OutingRequest outing = outingRequestRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Outing not found: " + id));

        outing.setStatus("APPROVED");
        outing.setApprovedBy(userId);
        outing.setApprovedAt(LocalDateTime.now());
        outing.setParentNotes(req.parentNotes());
        outing.setUpdatedAt(LocalDateTime.now());
        outing.markAsExisting();

        return outingConverter.toDTO(outingRequestRepository.save(outing));
    }

    public OutingDTO reject(UUID id, OutingRejectionRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Rejecting outing: {} by user: {}", id, userId);

        OutingRequest outing = outingRequestRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Outing not found: " + id));

        outing.setStatus("REJECTED");
        outing.setApprovedBy(userId);
        outing.setApprovedAt(LocalDateTime.now());
        outing.setRejectionReason(req.reason());
        outing.setUpdatedAt(LocalDateTime.now());
        outing.markAsExisting();

        return outingConverter.toDTO(outingRequestRepository.save(outing));
    }

    public OutingDTO depart(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Marking outing as departed: {}", id);

        OutingRequest outing = outingRequestRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Outing not found: " + id));

        outing.setStatus("DEPARTED");
        outing.setActualDeparture(LocalDateTime.now());
        outing.setUpdatedAt(LocalDateTime.now());
        outing.markAsExisting();

        return outingConverter.toDTO(outingRequestRepository.save(outing));
    }

    public OutingDTO returnHome(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Marking outing as returned: {}", id);

        OutingRequest outing = outingRequestRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Outing not found: " + id));

        LocalDateTime now = LocalDateTime.now();
        outing.setActualReturn(now);

        // Check if the return is past the estimated return time
        if (outing.getEstimatedReturnTime() != null && outing.getEventDate() != null) {
            LocalDateTime estimatedReturn = LocalDateTime.of(outing.getEventDate(), outing.getEstimatedReturnTime());
            if (now.isAfter(estimatedReturn)) {
                outing.setStatus("LATE");
            } else {
                outing.setStatus("RETURNED");
            }
        } else {
            outing.setStatus("RETURNED");
        }

        outing.setUpdatedAt(now);
        outing.markAsExisting();

        return outingConverter.toDTO(outingRequestRepository.save(outing));
    }
}
