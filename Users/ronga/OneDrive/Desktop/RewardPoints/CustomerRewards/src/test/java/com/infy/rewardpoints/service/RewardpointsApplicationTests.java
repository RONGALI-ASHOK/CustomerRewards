package com.infy.rewardpoints.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.infy.rewardpoints.dto.WrapperDTO;
import com.infy.rewardpoints.entity.RewardPoints;
import com.infy.rewardpoints.exception.RewardPointsException;
import com.infy.rewardpoints.repository.RewardPointsRepository;

@ExtendWith(MockitoExtension.class)
class RewardpointsApplicationTests {

    @Mock
    private RewardPointsRepository rewardPointsRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Test
    void calculateRewardPointsWhenNoPurchases() {

        Integer customerId = 1;
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 1);
        when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(eq(customerId), eq(from), eq(to)))
                .thenReturn(List.of());
        RewardPointsException ex = assertThrows(RewardPointsException.class,
                () -> rewardService.calculateRewardPoints(customerId, null, from, to));
        assertEquals("No purchase details found for the Customer.", ex.getMessage());
    }

    @Test
    void calculateRewardPointsComputesMonthlyAndTotalPoints() throws Exception {

        Integer customerId = 1;
        LocalDate to = LocalDate.of(2026, 3, 10);
        Integer months = 3;
        LocalDate expectedFrom = to.minusMonths(months);
        RewardPoints janHigh = purchase(1, customerId, "mahesh@gmail.com", "Mahesh", LocalDate.of(2026, 1, 15), 120);
        RewardPoints janMid = purchase(2, customerId, "mahesh@gmail.com", "Mahesh", LocalDate.of(2026, 1, 20), 70);
        RewardPoints febLow = purchase(3, customerId, "mahesh@gmail.com", "Mahesh", LocalDate.of(2026, 2, 5), 40);

        when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(eq(customerId), eq(expectedFrom), eq(to)))
                .thenReturn(List.of(janHigh, janMid, febLow));
        WrapperDTO result = rewardService.calculateRewardPoints(customerId, months, null, to);
        assertNotNull(result);
        assertNotNull(result.getRewardPointsDTO());
        assertEquals("Mahesh", result.getRewardPointsDTO().getName());
        assertEquals(customerId, result.getRewardPointsDTO().getCustomerId());
        assertEquals("mahesh@gmail.com", result.getRewardPointsDTO().getEmailId());
        assertEquals("Total Reward Points : 110", result.getRewardPointsDTO().getTotalPoints());
        assertEquals(110, result.getRewardPointsDTO().getMonthlyPoints().get("January"));
        assertEquals(0, result.getRewardPointsDTO().getMonthlyPoints().get("February"));

        ArgumentCaptor<LocalDate> fromCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(rewardPointsRepository).findByCustomerIdAndDateOfPurchaseBetween(eq(customerId), fromCaptor.capture(),
                eq(to));
    }

    @Test
    void calculateRewardPointsUsesExplicitFromDate() throws Exception {
        Integer customerId = 1;
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        RewardPoints purchase = purchase(1, customerId, "Hari.panakala@gmail.com", "Hari", LocalDate.of(2026, 1, 10),
                55);
        when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(eq(customerId), eq(from), eq(to)))
                .thenReturn(List.of(purchase));
        WrapperDTO result = rewardService.calculateRewardPoints(customerId, 99, from, to);
        assertNotNull(result);
        assertNotNull(result.getRewardPointsDTO());
        assertEquals("Hari", result.getRewardPointsDTO().getName());
        assertEquals(customerId, result.getRewardPointsDTO().getCustomerId());
        assertEquals("Hari.panakala@gmail.com", result.getRewardPointsDTO().getEmailId());
        assertEquals("Total Reward Points : 5", result.getRewardPointsDTO().getTotalPoints());
        assertEquals(5, result.getRewardPointsDTO().getMonthlyPoints().get("January"));
        verify(rewardPointsRepository).findByCustomerIdAndDateOfPurchaseBetween(eq(customerId), eq(from), eq(to));
    }

    @Test
    void getPurchaseDetailsWhenPurchasesExist() throws Exception {
        String email = "krishna.rongala@outlook.com";
        RewardPoints purchase1 = purchase(1, 1, email, "Krishna", LocalDate.of(2026, 1, 15), 120);
        RewardPoints purchase2 = purchase(2, 1, email, "Krishna", LocalDate.of(2026, 2, 20), 70);
        when(rewardPointsRepository.findByCustomerId(1)).thenReturn(List.of(purchase1, purchase2));
        var future = rewardService.getPurchaseDetails(1);
        var dtos = future.join();
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        verify(rewardPointsRepository).findByCustomerId(1);
    }

    @Test
    void calculateRewardPointsInvalidDateRangeThrowsException() {
        Integer customerId = 1;
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2025, 12, 31); // to is before from
        RewardPointsException ex = assertThrows(RewardPointsException.class,
                () -> rewardService.calculateRewardPoints(customerId, null, from, to));
        assertEquals("Enter the Date in correct format", ex.getMessage());
    }

    @Test
    void getPurchaseDetailsThrowsExceptionWhenNoPurchases() throws Exception {
        when(rewardPointsRepository.findByCustomerId(5)).thenReturn(List.of());
        RewardPointsException ex = assertThrows(RewardPointsException.class,
                () -> rewardService.getPurchaseDetails(5));
        assertEquals("No purchase details found for the Customer.", ex.getMessage());
    }

    @Test
    void getPurchaseDetailsReturnsSingleDTOWhenOnePurchaseExists() throws Exception {
        String email = "sainath.vanga@gmail.com";
        RewardPoints purchase = purchase(1, 1, email, "Sainath", LocalDate.of(2026, 3, 10), 200);
        when(rewardPointsRepository.findByCustomerId(1)).thenReturn(List.of(purchase));
        var future = rewardService.getPurchaseDetails(1);
        var dtos = future.join();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        verify(rewardPointsRepository).findByCustomerId(1);

    }

    private static RewardPoints purchase(int serialNumber, int customerId, String email, String name, LocalDate date,
            int amount) {

        RewardPoints rp = new RewardPoints();
        rp.setSerialNumber(serialNumber);
        rp.setCustomerId(customerId);
        rp.setEmailId(email);
        rp.setName(name);
        rp.setDateOfPurchase(date);
        rp.setAmount(amount);
        return rp;
    }
}
