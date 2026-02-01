package com.poc.tenant.membership.domain;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table("TNT_ACC_MEMBERSHIP_ROLES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipRole {

    @Column("MEMBERSHIP_ID")
    private UUID membershipId;

    @Column("ROLE_ID")
    private UUID roleId;
}
