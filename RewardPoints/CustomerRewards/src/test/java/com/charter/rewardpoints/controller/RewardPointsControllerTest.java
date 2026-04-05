package com.charter.rewardpoints.controller;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import com.charter.rewardpoints.dto.RewardCalculationResponse;
import com.charter.rewardpoints.exception.RewardPointsException;
import com.charter.rewardpoints.service.RewardService;

@ExtendWith(MockitoExtension.class)
@DisplayName("RewardPointsController Unit Tests")
class RewardPointsControllerTest {

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardPointsController controller;

    @Test
    @DisplayName("calculateRewardPoints - returns 200 with exact parameters")
    void testCalculateRewardPointsSuccess() {

        RewardCalculationResponse mockResponse = new RewardCalculationResponse();
        mockResponse.setTotalRewardPoints(150.0);
        when(rewardService.calculateRewardPoints(1, 2, null, null)).thenReturn(mockResponse);
        ResponseEntity<RewardCalculationResponse> response = controller.calculateRewardPoints(1, 2, null, null);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(150.0, response.getBody().getTotalRewardPoints());
        verify(rewardService).calculateRewardPoints(1, 2, null, null);
    }

    @Test
    @DisplayName("calculateRewardPoints - passes exact date range parameters to service")
    void testCalculateRewardPointsWithDateRange() {

        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 3, 1);
        RewardCalculationResponse mockResponse = new RewardCalculationResponse();
        when(rewardService.calculateRewardPoints(1, null, from, to)).thenReturn(mockResponse);
        ResponseEntity<RewardCalculationResponse> response = controller.calculateRewardPoints(1, null, from, to);
        assertEquals(200, response.getStatusCode().value());
        verify(rewardService).calculateRewardPoints(1, null, from, to);

    }

    @Test
    @DisplayName("calculateRewardPoints - propagates RewardPointsException")
    void testCalculateRewardPointsException() {

        when(rewardService.calculateRewardPoints(1, 1, null, null))
                .thenThrow(new RewardPointsException("Error"));
        RewardPointsException ex = assertThrows(RewardPointsException.class,
                () -> controller.calculateRewardPoints(1, 1, null, null));
        assertEquals("Error", ex.getMessage());
        verify(rewardService).calculateRewardPoints(1, 1, null, null);
    }
}
