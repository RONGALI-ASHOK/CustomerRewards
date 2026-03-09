package com.infy.rewardpoints;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.infy.rewardpoints.entity.RewardPoints;
import com.infy.rewardpoints.exception.RewardPointsException;
import com.infy.rewardpoints.repository.RewardPointsRepository;
import com.infy.rewardpoints.service.RewardServiceImpl;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private RewardPointsRepository rewardPointsRepository;

    @InjectMocks
    private RewardServiceImpl rewardService;

    @Test
    void calculateRewardPointsWhenNoPurchases() {

        String email = "ash.rongali@gmail.com";
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 1);
        when(rewardPointsRepository.findByEmailIdAndDateOfPurchaseBetween(eq(email), eq(from), eq(to))).thenReturn(List.of());
        RewardPointsException ex = assertThrows(RewardPointsException.class,
                () -> rewardService.calculateRewardPoints(email, null, from, to));
        assertEquals("No purchase details found for the Customer.", ex.getMessage());
    }

    @Test
    void calculateRewardPointsComputesMonthlyAndTotalPoints() throws Exception {

        String email = "mahesh@gmail.com";
        LocalDate to = LocalDate.of(2026, 3, 31);
        Integer months = 2;
        LocalDate expectedFrom = to.minusMonths(months);
        RewardPoints janHigh = purchase(1, email, "Mahesh", LocalDate.of(2026, 1, 15), 120); 
        RewardPoints janMid = purchase(2, email, "Mahesh", LocalDate.of(2026, 1, 20), 70);  
        RewardPoints febLow = purchase(3, email, "Mahesh", LocalDate.of(2026, 2, 5), 40);   
		
        when(rewardPointsRepository.findByEmailIdAndDateOfPurchaseBetween(eq(email), any(LocalDate.class), eq(to)))
                .thenReturn(List.of(janHigh, janMid, febLow));
        String result = rewardService.calculateRewardPoints(email, months, null, to);
        assertNotNull(result);
        assertTrue(result.contains("January: 110 points"), result);
        assertTrue(result.contains("February: 0 points"), result);
        assertTrue(result.contains("Total: 110 points"), result);

        ArgumentCaptor<LocalDate> fromCaptor = ArgumentCaptor.forClass(LocalDate.class);
        verify(rewardPointsRepository).findByEmailIdAndDateOfPurchaseBetween(eq(email), fromCaptor.capture(), eq(to));
        assertEquals(expectedFrom, fromCaptor.getValue());
    }

    @Test
    void calculateRewardPointsUsesExplicitFromDate() throws Exception {
        String email = "Hari.panakala@gmail.com";
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        RewardPoints purchase = purchase(1, email, "Hari", LocalDate.of(2026, 1, 10), 55); // 5 pts
        when(rewardPointsRepository.findByEmailIdAndDateOfPurchaseBetween(eq(email), eq(from), eq(to))).thenReturn(List.of(purchase));
        String result = rewardService.calculateRewardPoints(email, 99, from, to);
        assertTrue(result.contains("January: 5 points"), result);
        assertTrue(result.contains("Total: 5 points"), result);
        verify(rewardPointsRepository).findByEmailIdAndDateOfPurchaseBetween(eq(email), eq(from), eq(to));
    }

    @Test
    void getPurchaseDetailsWhenPurchasesExist() throws Exception {
        String email = "krishna.rongala@outlook.com";
        RewardPoints purchase1 = purchase(1, email, "Krishna", LocalDate.of(2026, 1, 15), 120);
        RewardPoints purchase2 = purchase(2, email, "Krishna", LocalDate.of(2026, 2, 20), 70);
        when(rewardPointsRepository.findByEmailId(email)).thenReturn(List.of(purchase1, purchase2));
        var future = rewardService.getPurchaseDetails(email);
        var dtos = future.join();
        assertNotNull(dtos);
        assertEquals(2, dtos.size());
        verify(rewardPointsRepository).findByEmailId(email);
    }

    @Test
    void getPurchaseDetailsReturnsSingleDTOWhenOnePurchaseExists() throws Exception {
        String email = "sainath.vanga@gmail.com";
        RewardPoints purchase = purchase(1, email, "Sainath", LocalDate.of(2026, 3, 10), 200);
        when(rewardPointsRepository.findByEmailId(email)).thenReturn(List.of(purchase));
        var future = rewardService.getPurchaseDetails(email);
        var dtos = future.join();
        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        verify(rewardPointsRepository).findByEmailId(email);

    }

    private static RewardPoints purchase(int serialNumber, String email, String name, LocalDate date, int amount) {

        RewardPoints rp = new RewardPoints();
        rp.setSerialNumber(serialNumber);
        rp.setEmailId(email);
        rp.setName(name);
        rp.setDateOfPurchase(date);
        rp.setAmount(amount);
        return rp;
    }
}


 
