package com.poc.lar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poc.lar.domain.BillPayment;
import com.poc.lar.domain.Chore;
import com.poc.lar.domain.ChoreLog;
import com.poc.lar.domain.FamilyMember;
import com.poc.lar.domain.HouseholdBill;
import com.poc.lar.domain.Reward;
import com.poc.lar.domain.RewardRedemption;
import com.poc.lar.dto.BillPaymentDTO;
import com.poc.lar.dto.BillPaymentRequest;
import com.poc.lar.dto.ChoreCompleteRequest;
import com.poc.lar.dto.ChoreDTO;
import com.poc.lar.dto.ChoreLogDTO;
import com.poc.lar.dto.ChoreRequest;
import com.poc.lar.dto.GamificationSummaryDTO;
import com.poc.lar.dto.HouseholdBillDTO;
import com.poc.lar.dto.HouseholdBillRequest;
import com.poc.lar.dto.RewardDTO;
import com.poc.lar.dto.RewardRedemptionDTO;
import com.poc.lar.dto.RewardRequest;
import com.poc.lar.repository.BillPaymentRepository;
import com.poc.lar.repository.ChoreLogRepository;
import com.poc.lar.repository.ChoreRepository;
import com.poc.lar.repository.FamilyMemberRepository;
import com.poc.lar.repository.HouseholdBillRepository;
import com.poc.lar.repository.RewardRedemptionRepository;
import com.poc.lar.repository.RewardRepository;
import com.poc.shared.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HouseService {

    private final HouseholdBillRepository householdBillRepository;
    private final BillPaymentRepository billPaymentRepository;
    private final ChoreRepository choreRepository;
    private final ChoreLogRepository choreLogRepository;
    private final RewardRepository rewardRepository;
    private final RewardRedemptionRepository rewardRedemptionRepository;
    private final FamilyMemberRepository familyMemberRepository;
    private final ObjectMapper objectMapper;

    // ========== Bills ==========

    public List<HouseholdBillDTO> findAllBills() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all household bills for tenant: {}", tenantId);
        return householdBillRepository.findByTenantId(tenantId).stream()
                .map(this::toBillDTO)
                .toList();
    }

    public HouseholdBillDTO createBill(HouseholdBillRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating household bill for tenant: {}", tenantId);

        HouseholdBill bill = HouseholdBill.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(req.name())
                .category(req.category())
                .amountCents(req.amountCents())
                .dueDay(req.dueDay())
                .frequency(req.frequency())
                .autoPay(req.autoPay() != null ? req.autoPay() : false)
                .responsibleMemberId(req.responsibleMemberId())
                .notes(req.notes())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toBillDTO(householdBillRepository.save(bill));
    }

    public HouseholdBillDTO updateBill(UUID id, HouseholdBillRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating household bill: {} for tenant: {}", id, tenantId);

        HouseholdBill bill = householdBillRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Household bill not found: " + id));

        bill.setName(req.name());
        bill.setCategory(req.category());
        bill.setAmountCents(req.amountCents());
        bill.setDueDay(req.dueDay());
        bill.setFrequency(req.frequency());
        bill.setAutoPay(req.autoPay());
        bill.setResponsibleMemberId(req.responsibleMemberId());
        bill.setNotes(req.notes());
        bill.setUpdatedAt(LocalDateTime.now());
        bill.markAsExisting();

        return toBillDTO(householdBillRepository.save(bill));
    }

    // ========== Payments ==========

    public List<BillPaymentDTO> findPaymentsByBill(UUID billId) {
        log.debug("Finding payments for bill: {}", billId);
        return billPaymentRepository.findByBillId(billId).stream()
                .map(this::toPaymentDTO)
                .toList();
    }

    public BillPaymentDTO pay(UUID billId, BillPaymentRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Recording payment for bill: {}", billId);

        // Verify the bill belongs to the tenant
        householdBillRepository.findByIdAndTenantId(billId, tenantId)
                .orElseThrow(() -> new RuntimeException("Household bill not found: " + billId));

        BillPayment payment = BillPayment.builder()
                .id(UUID.randomUUID())
                .billId(billId)
                .referenceMonth(req.referenceMonth())
                .amountCents(req.amountCents())
                .status("PAID")
                .paidAt(LocalDateTime.now())
                .paidBy(userId)
                .receiptUrl(req.receiptUrl())
                .notes(req.notes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toPaymentDTO(billPaymentRepository.save(payment));
    }

    public List<BillPaymentDTO> findOverdue() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding overdue payments for tenant: {}", tenantId);
        return billPaymentRepository.findOverdue(tenantId).stream()
                .map(this::toPaymentDTO)
                .toList();
    }

    public Map<String, Integer> getSummary() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Getting bill summary for tenant: {}", tenantId);

        List<HouseholdBill> bills = householdBillRepository.findByTenantId(tenantId);
        Map<String, Integer> summary = new HashMap<>();

        for (HouseholdBill bill : bills) {
            String category = bill.getCategory() != null ? bill.getCategory() : "OTHER";
            summary.merge(category, bill.getAmountCents() != null ? bill.getAmountCents() : 0, Integer::sum);
        }

        return summary;
    }

    // ========== Chores ==========

    public List<ChoreDTO> findAllChores() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all chores for tenant: {}", tenantId);
        return choreRepository.findByTenantId(tenantId).stream()
                .map(this::toChoreDTO)
                .toList();
    }

    public List<ChoreDTO> findTodayChores() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding today's chores for tenant: {}", tenantId);

        int todayDow = LocalDate.now().getDayOfWeek().getValue(); // 1=Monday .. 7=Sunday
        List<Chore> allChores = choreRepository.findByTenantId(tenantId);

        return allChores.stream()
                .filter(chore -> {
                    if ("DAILY".equalsIgnoreCase(chore.getFrequency())) {
                        return true;
                    }
                    if ("WEEKLY".equalsIgnoreCase(chore.getFrequency()) && chore.getDayOfWeek() != null) {
                        return chore.getDayOfWeek().equals(todayDow);
                    }
                    return false;
                })
                .map(this::toChoreDTO)
                .toList();
    }

    public ChoreDTO createChore(ChoreRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating chore for tenant: {}", tenantId);

        Chore chore = Chore.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(req.name())
                .description(req.description())
                .frequency(req.frequency())
                .dayOfWeek(req.dayOfWeek())
                .points(req.points() != null ? req.points() : 0)
                .assignmentType(req.assignmentType())
                .assignedTo(req.assignedTo())
                .rotationMembers(serializeUUIDList(req.rotationMembers()))
                .rotationIndex(0)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toChoreDTO(choreRepository.save(chore));
    }

    public ChoreDTO updateChore(UUID id, ChoreRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Updating chore: {} for tenant: {}", id, tenantId);

        Chore chore = choreRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Chore not found: " + id));

        chore.setName(req.name());
        chore.setDescription(req.description());
        chore.setFrequency(req.frequency());
        chore.setDayOfWeek(req.dayOfWeek());
        chore.setPoints(req.points());
        chore.setAssignmentType(req.assignmentType());
        chore.setAssignedTo(req.assignedTo());
        chore.setRotationMembers(serializeUUIDList(req.rotationMembers()));
        chore.setUpdatedAt(LocalDateTime.now());
        chore.markAsExisting();

        return toChoreDTO(choreRepository.save(chore));
    }

    public ChoreLogDTO completeChore(UUID id, ChoreCompleteRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Completing chore: {} by user: {}", id, userId);

        Chore chore = choreRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Chore not found: " + id));

        FamilyMember member = familyMemberRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new RuntimeException("Family member not found for user: " + userId));

        ChoreLog choreLog = ChoreLog.builder()
                .id(UUID.randomUUID())
                .choreId(chore.getId())
                .memberId(member.getId())
                .completedAt(LocalDateTime.now())
                .photoUrl(req.photoUrl())
                .pointsEarned(chore.getPoints() != null ? chore.getPoints() : 0)
                .note(req.note())
                .createdAt(LocalDateTime.now())
                .build();

        ChoreLog saved = choreLogRepository.save(choreLog);

        return new ChoreLogDTO(
                saved.getId(),
                saved.getChoreId(),
                chore.getName(),
                saved.getMemberId(),
                member.getNickname(),
                saved.getCompletedAt(),
                saved.getVerifiedBy(),
                saved.getVerifiedAt(),
                saved.getPhotoUrl(),
                saved.getPointsEarned(),
                saved.getNote(),
                saved.getCreatedAt()
        );
    }

    public ChoreLogDTO verifyChore(UUID choreLogId) {
        UUID userId = TenantContext.getCurrentUser();
        log.info("Verifying chore log: {} by user: {}", choreLogId, userId);

        ChoreLog choreLog = choreLogRepository.findById(choreLogId)
                .orElseThrow(() -> new RuntimeException("Chore log not found: " + choreLogId));

        choreLog.setVerifiedBy(userId);
        choreLog.setVerifiedAt(LocalDateTime.now());
        choreLog.markAsExisting();

        ChoreLog saved = choreLogRepository.save(choreLog);

        // Lookup names for the DTO
        Chore chore = choreRepository.findById(saved.getChoreId()).orElse(null);
        UUID tenantId = TenantContext.getCurrentTenant();
        FamilyMember member = familyMemberRepository.findByIdAndTenantId(saved.getMemberId(), tenantId).orElse(null);

        return new ChoreLogDTO(
                saved.getId(),
                saved.getChoreId(),
                chore != null ? chore.getName() : null,
                saved.getMemberId(),
                member != null ? member.getNickname() : null,
                saved.getCompletedAt(),
                saved.getVerifiedBy(),
                saved.getVerifiedAt(),
                saved.getPhotoUrl(),
                saved.getPointsEarned(),
                saved.getNote(),
                saved.getCreatedAt()
        );
    }

    public List<ChoreLogDTO> findChoreLog(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding chore log for member: {} in tenant: {}", memberId, tenantId);

        List<ChoreLog> logs = choreLogRepository.findByMemberId(memberId);
        return logs.stream()
                .map(choreLog -> {
                    Chore chore = choreRepository.findById(choreLog.getChoreId()).orElse(null);
                    FamilyMember member = familyMemberRepository.findByIdAndTenantId(choreLog.getMemberId(), tenantId).orElse(null);
                    return new ChoreLogDTO(
                            choreLog.getId(),
                            choreLog.getChoreId(),
                            chore != null ? chore.getName() : null,
                            choreLog.getMemberId(),
                            member != null ? member.getNickname() : null,
                            choreLog.getCompletedAt(),
                            choreLog.getVerifiedBy(),
                            choreLog.getVerifiedAt(),
                            choreLog.getPhotoUrl(),
                            choreLog.getPointsEarned(),
                            choreLog.getNote(),
                            choreLog.getCreatedAt()
                    );
                })
                .toList();
    }

    // ========== Gamification ==========

    public GamificationSummaryDTO getPoints(UUID memberId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Getting points for member: {} in tenant: {}", memberId, tenantId);

        Integer totalEarned = choreLogRepository.sumPointsByMemberId(memberId);
        Integer totalSpent = rewardRedemptionRepository.sumPointsSpentByMemberId(memberId);
        int balance = totalEarned - totalSpent;

        List<ChoreLog> logs = choreLogRepository.findByMemberId(memberId);
        int completedChores = logs.size();

        FamilyMember member = familyMemberRepository.findByIdAndTenantId(memberId, tenantId).orElse(null);
        String nickname = member != null ? member.getNickname() : null;

        return new GamificationSummaryDTO(
                memberId,
                nickname,
                totalEarned,
                totalSpent,
                balance,
                completedChores
        );
    }

    public List<GamificationSummaryDTO> getLeaderboard() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Getting leaderboard for tenant: {}", tenantId);

        List<FamilyMember> members = familyMemberRepository.findByTenantId(tenantId);
        return members.stream()
                .filter(m -> Boolean.TRUE.equals(m.getActive()))
                .map(member -> {
                    Integer earned = choreLogRepository.sumPointsByMemberId(member.getId());
                    Integer spent = rewardRedemptionRepository.sumPointsSpentByMemberId(member.getId());
                    int balance = earned - spent;
                    int completedChores = choreLogRepository.findByMemberId(member.getId()).size();
                    return new GamificationSummaryDTO(
                            member.getId(),
                            member.getNickname(),
                            earned,
                            spent,
                            balance,
                            completedChores
                    );
                })
                .sorted((a, b) -> b.currentBalance().compareTo(a.currentBalance()))
                .toList();
    }

    // ========== Rewards ==========

    public List<RewardDTO> findAllRewards() {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.debug("Finding all rewards for tenant: {}", tenantId);
        return rewardRepository.findByTenantId(tenantId).stream()
                .map(this::toRewardDTO)
                .toList();
    }

    public RewardDTO createReward(RewardRequest req) {
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Creating reward for tenant: {}", tenantId);

        Reward reward = Reward.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(req.name())
                .description(req.description())
                .pointsCost(req.pointsCost())
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toRewardDTO(rewardRepository.save(reward));
    }

    public RewardRedemptionDTO redeemReward(UUID rewardId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        UUID userId = TenantContext.getCurrentUser();
        log.info("Redeeming reward: {} by user: {}", rewardId, userId);

        Reward reward = rewardRepository.findByIdAndTenantId(rewardId, tenantId)
                .orElseThrow(() -> new RuntimeException("Reward not found: " + rewardId));

        FamilyMember member = familyMemberRepository.findByUserIdAndTenantId(userId, tenantId)
                .orElseThrow(() -> new RuntimeException("Family member not found for user: " + userId));

        // Check if member has enough points
        Integer earned = choreLogRepository.sumPointsByMemberId(member.getId());
        Integer spent = rewardRedemptionRepository.sumPointsSpentByMemberId(member.getId());
        int balance = earned - spent;

        if (balance < reward.getPointsCost()) {
            throw new RuntimeException("Insufficient points. Current balance: " + balance + ", cost: " + reward.getPointsCost());
        }

        RewardRedemption redemption = RewardRedemption.builder()
                .id(UUID.randomUUID())
                .rewardId(rewardId)
                .memberId(member.getId())
                .pointsSpent(reward.getPointsCost())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        RewardRedemption saved = rewardRedemptionRepository.save(redemption);

        return new RewardRedemptionDTO(
                saved.getId(),
                saved.getRewardId(),
                reward.getName(),
                saved.getMemberId(),
                member.getNickname(),
                saved.getPointsSpent(),
                saved.getStatus(),
                saved.getApprovedBy(),
                saved.getRedeemedAt(),
                saved.getCreatedAt()
        );
    }

    public RewardRedemptionDTO approveRedemption(UUID redemptionId) {
        UUID userId = TenantContext.getCurrentUser();
        UUID tenantId = TenantContext.getCurrentTenant();
        log.info("Approving redemption: {} by user: {}", redemptionId, userId);

        RewardRedemption redemption = rewardRedemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new RuntimeException("Redemption not found: " + redemptionId));

        redemption.setStatus("APPROVED");
        redemption.setApprovedBy(userId);
        redemption.setRedeemedAt(LocalDateTime.now());
        redemption.markAsExisting();

        RewardRedemption saved = rewardRedemptionRepository.save(redemption);

        // Lookup names for DTO
        Reward reward = rewardRepository.findById(saved.getRewardId()).orElse(null);
        FamilyMember member = familyMemberRepository.findByIdAndTenantId(saved.getMemberId(), tenantId).orElse(null);

        return new RewardRedemptionDTO(
                saved.getId(),
                saved.getRewardId(),
                reward != null ? reward.getName() : null,
                saved.getMemberId(),
                member != null ? member.getNickname() : null,
                saved.getPointsSpent(),
                saved.getStatus(),
                saved.getApprovedBy(),
                saved.getRedeemedAt(),
                saved.getCreatedAt()
        );
    }

    // ========== Private Helpers ==========

    private HouseholdBillDTO toBillDTO(HouseholdBill bill) {
        // Find last payment status
        List<BillPayment> payments = billPaymentRepository.findByBillId(bill.getId());
        String lastPaymentStatus = payments.isEmpty() ? null : payments.get(0).getStatus();

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

    private BillPaymentDTO toPaymentDTO(BillPayment payment) {
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

    private ChoreDTO toChoreDTO(Chore chore) {
        UUID tenantId = TenantContext.getCurrentTenant();
        List<UUID> rotationMembersList = parseUUIDList(chore.getRotationMembers());

        // Determine current assignee name
        String currentAssigneeName = null;
        if (chore.getAssignedTo() != null) {
            currentAssigneeName = familyMemberRepository.findByIdAndTenantId(chore.getAssignedTo(), tenantId)
                    .map(FamilyMember::getNickname)
                    .orElse(null);
        } else if ("ROTATION".equalsIgnoreCase(chore.getAssignmentType()) && !rotationMembersList.isEmpty() && chore.getRotationIndex() != null) {
            int idx = chore.getRotationIndex() % rotationMembersList.size();
            UUID currentMemberId = rotationMembersList.get(idx);
            currentAssigneeName = familyMemberRepository.findByIdAndTenantId(currentMemberId, tenantId)
                    .map(FamilyMember::getNickname)
                    .orElse(null);
        }

        return new ChoreDTO(
                chore.getId(),
                chore.getName(),
                chore.getDescription(),
                chore.getFrequency(),
                chore.getDayOfWeek(),
                chore.getPoints(),
                chore.getAssignmentType(),
                chore.getAssignedTo(),
                rotationMembersList,
                chore.getRotationIndex(),
                currentAssigneeName,
                chore.getActive(),
                chore.getCreatedAt(),
                chore.getUpdatedAt()
        );
    }

    private RewardDTO toRewardDTO(Reward reward) {
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

    private String serializeUUIDList(List<UUID> list) {
        if (list == null || list.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Failed to serialize UUID list: {}", list);
            return null;
        }
    }

    private List<UUID> parseUUIDList(String json) {
        if (json == null || json.isBlank()) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<UUID>>() {});
        } catch (Exception e) {
            log.warn("Failed to parse UUID list: {}", json);
            return Collections.emptyList();
        }
    }
}
