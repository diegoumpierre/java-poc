package com.poc.lar.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.*;
import com.poc.lar.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class HouseConverter {

    private final ObjectMapper objectMapper;

    // --- HouseholdBill ---

    public HouseholdBillDTO toDTO(HouseholdBill bill, String lastPaymentStatus) {
        return new HouseholdBillDTO(
            bill.getId(),
            bill.getName(),
            bill.getCategory(),
            bill.getAmountCents(),
            bill.getDueDay(),
            bill.getFrequency(),
            bill.getAutoPay(),
            bill.getResponsibleMemberId(),
            bill.getNotes(),
            bill.getActive(),
            lastPaymentStatus,
            bill.getCreatedAt(),
            bill.getUpdatedAt()
        );
    }

    public HouseholdBillDTO toDTO(HouseholdBill bill) {
        return toDTO(bill, null);
    }

    public List<HouseholdBillDTO> toBillDTOList(List<HouseholdBill> bills) {
        return bills.stream().map(this::toDTO).toList();
    }

    // --- BillPayment ---

    public BillPaymentDTO toDTO(BillPayment payment) {
        return new BillPaymentDTO(
            payment.getId(),
            payment.getBillId(),
            payment.getReferenceMonth(),
            payment.getAmountCents(),
            payment.getStatus(),
            payment.getPaidAt(),
            payment.getPaidBy(),
            payment.getReceiptUrl(),
            payment.getNotes(),
            payment.getCreatedAt(),
            payment.getUpdatedAt()
        );
    }

    public List<BillPaymentDTO> toPaymentDTOList(List<BillPayment> payments) {
        return payments.stream().map(this::toDTO).toList();
    }

    // --- Chore ---

    public ChoreDTO toDTO(Chore chore, String currentAssigneeName) {
        return new ChoreDTO(
            chore.getId(),
            chore.getName(),
            chore.getDescription(),
            chore.getFrequency(),
            chore.getDayOfWeek(),
            chore.getPoints(),
            chore.getAssignmentType(),
            chore.getAssignedTo(),
            parseUuidList(chore.getRotationMembers()),
            chore.getRotationIndex(),
            currentAssigneeName,
            chore.getActive(),
            chore.getCreatedAt(),
            chore.getUpdatedAt()
        );
    }

    public ChoreDTO toDTO(Chore chore) {
        return toDTO(chore, null);
    }

    public List<ChoreDTO> toChoreDTOList(List<Chore> chores) {
        return chores.stream().map(this::toDTO).toList();
    }

    public String serializeUuidList(List<UUID> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to serialize UUID list: {}", list);
            return null;
        }
    }

    // --- ChoreLog ---

    public ChoreLogDTO toDTO(ChoreLog choreLog, String choreName, String memberNickname) {
        return new ChoreLogDTO(
            choreLog.getId(),
            choreLog.getChoreId(),
            choreName,
            choreLog.getMemberId(),
            memberNickname,
            choreLog.getCompletedAt(),
            choreLog.getVerifiedBy(),
            choreLog.getVerifiedAt(),
            choreLog.getPhotoUrl(),
            choreLog.getPointsEarned(),
            choreLog.getNote(),
            choreLog.getCreatedAt()
        );
    }

    public ChoreLogDTO toDTO(ChoreLog choreLog) {
        return toDTO(choreLog, null, null);
    }

    public List<ChoreLogDTO> toChoreLogDTOList(List<ChoreLog> logs) {
        return logs.stream().map(this::toDTO).toList();
    }

    // --- Reward ---

    public RewardDTO toDTO(Reward reward) {
        return new RewardDTO(
            reward.getId(),
            reward.getName(),
            reward.getDescription(),
            reward.getPointsCost(),
            reward.getActive(),
            reward.getCreatedAt(),
            reward.getUpdatedAt()
        );
    }

    public List<RewardDTO> toRewardDTOList(List<Reward> rewards) {
        return rewards.stream().map(this::toDTO).toList();
    }

    // --- RewardRedemption ---

    public RewardRedemptionDTO toDTO(RewardRedemption redemption, String rewardName, String memberNickname) {
        return new RewardRedemptionDTO(
            redemption.getId(),
            redemption.getRewardId(),
            rewardName,
            redemption.getMemberId(),
            memberNickname,
            redemption.getPointsSpent(),
            redemption.getStatus(),
            redemption.getApprovedBy(),
            redemption.getRedeemedAt(),
            redemption.getCreatedAt()
        );
    }

    public RewardRedemptionDTO toDTO(RewardRedemption redemption) {
        return toDTO(redemption, null, null);
    }

    public List<RewardRedemptionDTO> toRedemptionDTOList(List<RewardRedemption> redemptions) {
        return redemptions.stream().map(this::toDTO).toList();
    }

    // --- JSON helpers ---

    private List<UUID> parseUuidList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            List<String> strings = objectMapper.readValue(json, new TypeReference<List<String>>() {});
            return strings.stream().map(UUID::fromString).toList();
        } catch (Exception e) {
            log.warn("Failed to parse UUID list JSON: {}", json);
            return Collections.emptyList();
        }
    }
}
