package com.poc.lar.service;

import com.poc.lar.converter.ContactConverter;
import com.poc.lar.domain.Contact;
import com.poc.lar.dto.ContactDTO;
import com.poc.lar.dto.ContactRequest;
import com.poc.lar.repository.ContactRepository;
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
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactConverter contactConverter;

    public List<ContactDTO> findAll() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all contacts for tenant: {}", tenantId);
        return contactConverter.toDTOList(contactRepository.findByTenantId(tenantId));
    }

    public Optional<ContactDTO> findById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding contact by id: {} for tenant: {}", id, tenantId);
        return contactRepository.findByIdAndTenantId(id, tenantId)
                .map(contactConverter::toDTO);
    }

    public List<ContactDTO> findByMemberId(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding contacts for member: {} in tenant: {}", memberId, tenantId);
        return contactConverter.toDTOList(contactRepository.findByMemberId(memberId, tenantId));
    }

    public List<ContactDTO> findTrusted() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding trusted contacts for tenant: {}", tenantId);
        return contactConverter.toDTOList(contactRepository.findTrusted(tenantId));
    }

    public ContactDTO create(ContactRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating contact for tenant: {}", tenantId);

        Contact contact = Contact.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .memberId(req.memberId())
                .name(req.name())
                .phone(req.phone())
                .relationship(req.relationship())
                .age(req.age())
                .whereMet(req.whereMet())
                .schoolName(req.schoolName())
                .parentName(req.parentName())
                .parentPhone(req.parentPhone())
                .parent2Name(req.parent2Name())
                .parent2Phone(req.parent2Phone())
                .address(req.address())
                .trusted(req.trusted() != null ? req.trusted() : false)
                .notes(req.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return contactConverter.toDTO(contactRepository.save(contact));
    }

    public ContactDTO update(UUID id, ContactRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating contact: {} for tenant: {}", id, tenantId);

        Contact contact = contactRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Contact not found: " + id));

        contact.setMemberId(req.memberId());
        contact.setName(req.name());
        contact.setPhone(req.phone());
        contact.setRelationship(req.relationship());
        contact.setAge(req.age());
        contact.setWhereMet(req.whereMet());
        contact.setSchoolName(req.schoolName());
        contact.setParentName(req.parentName());
        contact.setParentPhone(req.parentPhone());
        contact.setParent2Name(req.parent2Name());
        contact.setParent2Phone(req.parent2Phone());
        contact.setAddress(req.address());
        contact.setTrusted(req.trusted());
        contact.setNotes(req.notes());
        contact.setUpdatedAt(LocalDateTime.now());
        contact.markAsExisting();

        return contactConverter.toDTO(contactRepository.save(contact));
    }

    public void delete(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Deleting contact: {} for tenant: {}", id, tenantId);

        Contact contact = contactRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Contact not found: " + id));

        contactRepository.delete(contact);
    }
}
