package com.poc.lar.converter;

import com.poc.lar.domain.Contact;
import com.poc.lar.dto.ContactDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactConverter {

    public ContactDTO toDTO(Contact contact) {
        return new ContactDTO(
            contact.getId(),
            contact.getMemberId(),
            contact.getName(),
            contact.getPhone(),
            contact.getRelationship(),
            contact.getAge(),
            contact.getWhereMet(),
            contact.getSchoolName(),
            contact.getParentName(),
            contact.getParentPhone(),
            contact.getParent2Name(),
            contact.getParent2Phone(),
            contact.getAddress(),
            contact.getTrusted(),
            contact.getNotes(),
            contact.getCreatedAt(),
            contact.getUpdatedAt()
        );
    }

    public List<ContactDTO> toDTOList(List<Contact> contacts) {
        return contacts.stream().map(this::toDTO).toList();
    }
}
