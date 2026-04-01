package com.charter.rewardpoints.service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.charter.rewardpoints.dto.CustomerPurchaseDetails;
import com.charter.rewardpoints.dto.RewardCalculationResponse;
import com.charter.rewardpoints.entity.RewardPoints;
import com.charter.rewardpoints.exception.RewardPointsException;
import com.charter.rewardpoints.repository.RewardPointsRepository;
import com.charter.rewardpoints.utility.RewardValidator;

@ExtendWith(MockitoExtension.class)
@DisplayName("RewardServiceImpl Tests")
class RewardpointsServiceTest {

        @Mock
        private RewardPointsRepository rewardPointsRepository;

        @Mock
        private RewardValidator rewardValidator;

        @Mock
        private ModelMapper modelMapper;

        @InjectMocks
        private RewardServiceImpl rewardService;

        private List<RewardPoints> mockPurchaseList;

        @BeforeEach
        @SuppressWarnings("unused")
        void setUp() {

                RewardPoints purchase1 = new RewardPoints();
                purchase1.setCustomerId(1);
                purchase1.setAmount(new BigDecimal("150"));
                purchase1.setDateOfPurchase(LocalDate.of(2026, 1, 15));
                purchase1.setName("Rongali Ashok");
                purchase1.setEmailId("rongali.ashok@gmail.com");

                RewardPoints purchase2 = new RewardPoints();
                purchase2.setCustomerId(1);
                purchase2.setAmount(new BigDecimal("75"));
                purchase2.setDateOfPurchase(LocalDate.of(2026, 2, 20));
                purchase2.setName("Rongali Ashok");
                purchase2.setEmailId("rongali.ashok@gmail.com");

                mockPurchaseList = new ArrayList<>();
                mockPurchaseList.add(purchase1);
                mockPurchaseList.add(purchase2);

        }

        private void setupValidatorAndRepository(Integer customerId, LocalDate from, LocalDate to,
                        List<RewardPoints> purchases) {

                when(rewardValidator.determineEffectiveDateRange(any(), any(), any()))
                                .thenReturn(new LocalDate[] { from, to });
                when(rewardPointsRepository.findByCustomerId(customerId))
                                .thenReturn(List.of(new RewardPoints()));
                when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(customerId, from, to))
                                .thenReturn(purchases);
                doReturn(new ArrayList<CustomerPurchaseDetails>())
                                .when(modelMapper).map(any(), any(Type.class));
        }

        @Test
        @SuppressWarnings("ThrowableResultIgnored")
        @DisplayName("validates input parameters are passed correctly")
        void testValidationThrowsException() {

                doThrow(new RewardPointsException("Invalid input"))
                                .when(rewardValidator)
                                .validateInputParameters(2, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 1));

                RewardPointsException ex = assertThrows(RewardPointsException.class, () -> rewardService
                                .calculateRewardPoints(1, 2, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 1)));
                assertEquals("Invalid input", ex.getMessage());
                verify(rewardValidator).validateInputParameters(2, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 1));
        }

        @Test
        @DisplayName("throws exception when customer does not exist")
        void testCustomerNotExists() {

                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 3, 1);
                when(rewardValidator.determineEffectiveDateRange(null, null, null))
                                .thenReturn(new LocalDate[] { from, to });
                when(rewardPointsRepository.findByCustomerId(999))
                                .thenReturn(List.of());

                RewardPointsException ex = assertThrows(RewardPointsException.class,
                                () -> rewardService.calculateRewardPoints(999, null, null, null));
                assertEquals("There is no customer with the given Id.", ex.getMessage());
                verify(rewardPointsRepository).findByCustomerId(999);

        }

        @Test
        @DisplayName("throws exception when no purchase details found in date range")
        void testNoPurchaseDetailsThrowsException() {

                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 3, 1);
                when(rewardValidator.determineEffectiveDateRange(2, null, null))
                                .thenReturn(new LocalDate[] { from, to });
                when(rewardPointsRepository.findByCustomerId(1))
                                .thenReturn(List.of(new RewardPoints()));
                when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(1, from, to))
                                .thenReturn(new ArrayList<>());
                RewardPointsException ex = assertThrows(RewardPointsException.class,
                                () -> rewardService.calculateRewardPoints(1, 2, null, null));
                assertEquals("No purchase details found for the Customer for the specified period.", ex.getMessage());

        }

        @Test
        @DisplayName("returns correct total points, customer details, and monthly breakdown")
        void testCalculateRewardPointsSuccess() {

                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 3, 1);
                setupValidatorAndRepository(1, from, to, mockPurchaseList);
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 2, null, null);
                assertNotNull(response);
                assertEquals(175, response.getTotalRewardPoints());
                assertEquals(2, response.getMonthlyRewardPoints().size());
                assertEquals(1, response.getCustomerDetails().getCustomerId());
                assertEquals("Rongali Ashok", response.getCustomerDetails().getName());
                assertEquals("rongali.ashok@gmail.com", response.getCustomerDetails().getEmailId());
                assertTrue(response.getMonthlyRewardPoints().stream()
                                .anyMatch(m -> m.getMonth().equals("January") && m.getPoints()
                                .equals(150)));

                assertTrue(response.getMonthlyRewardPoints().stream()
                                .anyMatch(m -> m.getMonth().equals("February") && m.getPoints()
                                .equals(25)));
                verify(rewardPointsRepository).findByCustomerIdAndDateOfPurchaseBetween(1, from, to);
        }

        @Test
        @DisplayName("uses exact provided date range parameters")
        void testUsesProvidedDateRange() {

                LocalDate fromDate = LocalDate.of(2026, 1, 1);
                LocalDate toDate = LocalDate.of(2026, 2, 28);
                when(rewardValidator.determineEffectiveDateRange(null, fromDate, toDate))
                                .thenReturn(new LocalDate[] { fromDate, toDate });
                when(rewardPointsRepository.findByCustomerId(1))
                                .thenReturn(List.of(new RewardPoints()));
                when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(1, fromDate, toDate))
                                .thenReturn(mockPurchaseList);
                doReturn(new ArrayList<CustomerPurchaseDetails>())
                                .when(modelMapper).map(any(), any(Type.class));
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, null, fromDate, toDate);
                assertNotNull(response);
                assertEquals(175, response.getTotalRewardPoints());
                verify(rewardValidator).determineEffectiveDateRange(null, fromDate, toDate);
                verify(rewardPointsRepository).findByCustomerIdAndDateOfPurchaseBetween(1, fromDate, toDate);

        }

        @Test
        @DisplayName("groups and sums multiple purchases in the same month")
        void testMultiplePurchasesSameMonth() {

                RewardPoints p1 = new RewardPoints();
                p1.setCustomerId(1);
                p1.setAmount(new BigDecimal("100"));
                p1.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p1.setName("Rongali Ashok");
                p1.setEmailId("rongali.ashok@gmail.com");

                RewardPoints p2 = new RewardPoints();
                p2.setCustomerId(1);
                p2.setAmount(new BigDecimal("120"));
                p2.setDateOfPurchase(LocalDate.of(2026, 1, 25));
                p2.setName("Rongali Ashok");
                p2.setEmailId("rongali.ashok@gmail.com");

                List<RewardPoints> sameMonthList = List.of(p1, p2);
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, sameMonthList);
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(1, response.getMonthlyRewardPoints().size());
                assertEquals(140, response.getMonthlyRewardPoints().get(0).getPoints());
                assertEquals("January", response.getMonthlyRewardPoints().get(0).getMonth());

        }

        @Test
        @DisplayName("multiple transactions same month with varying amounts")
        void testMultipleTransactionsSameMonthVaryingAmounts() {

                RewardPoints p1 = new RewardPoints();
                p1.setCustomerId(1);
                p1.setAmount(new BigDecimal("30"));
                p1.setDateOfPurchase(LocalDate.of(2026, 1, 5));
                p1.setName("Rongali Ashok");
                p1.setEmailId("rongali.ashok@gmail.com");

                RewardPoints p2 = new RewardPoints();
                p2.setCustomerId(1);
                p2.setAmount(new BigDecimal("75"));
                p2.setDateOfPurchase(LocalDate.of(2026, 1, 15));
                p2.setName("Rongali Ashok");
                p2.setEmailId("rongali.ashok@gmail.com");

                RewardPoints p3 = new RewardPoints();
                p3.setCustomerId(1);
                p3.setAmount(new BigDecimal("200"));
                p3.setDateOfPurchase(LocalDate.of(2026, 1, 25));
                p3.setName("Rongali Ashok");
                p3.setEmailId("rongali.ashok@gmail.com");
                List<RewardPoints> list = List.of(p1, p2, p3);
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, list);
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(1, response.getMonthlyRewardPoints().size());
                assertEquals(275, response.getMonthlyRewardPoints().get(0).getPoints());
        }

        @Test
        @DisplayName("transactions spanning year boundaries (Dec 2025 + Jan 2026)")
        void testTransactionsSpanningYearBoundaries() {

                RewardPoints dec = new RewardPoints();
                dec.setCustomerId(1);
                dec.setAmount(new BigDecimal("120"));
                dec.setDateOfPurchase(LocalDate.of(2025, 12, 20));
                dec.setName("Rongali Ashok");
                dec.setEmailId("rongali.ashok@gmail.com");

                RewardPoints jan = new RewardPoints();
                jan.setCustomerId(1);
                jan.setAmount(new BigDecimal("80"));
                jan.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                jan.setName("Rongali Ashok");
                jan.setEmailId("rongali.ashok@gmail.com");

                List<RewardPoints> yearBoundaryList = List.of(dec, jan);
                LocalDate from = LocalDate.of(2025, 12, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, yearBoundaryList);
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, null, null, null);
                assertEquals(2, response.getMonthlyRewardPoints().size());
                assertTrue(response.getMonthlyRewardPoints().stream()
                                .anyMatch(m -> m.getMonth().equals("December") && m.getYear() == 2025 && m.getPoints().equals(90)));
                assertTrue(response.getMonthlyRewardPoints().stream()
                                .anyMatch(m -> m.getMonth().equals("January") && m.getYear() == 2026 && m.getPoints().equals(30)));

        }

        @Test
        @DisplayName("amount exactly $50 earns 0 points")
        void testAmountExactly50() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(new BigDecimal("50"));
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, List.of(p));
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(0, response.getTotalRewardPoints());
        }

        @Test
        @DisplayName("amount exactly $100 earns 50 points")
        void testAmountExactly100() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(new BigDecimal("100"));
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");

                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);

                setupValidatorAndRepository(1, from, to, List.of(p));
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(50, response.getTotalRewardPoints());
        }

        @Test
        @DisplayName("amount $0 earns 0 points")
        void testAmountZero() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(new BigDecimal("0"));
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, List.of(p));
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(0, response.getTotalRewardPoints());
        }

        @Test
        @DisplayName("negative amount earns 0 points")
        void testNegativeAmount() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(new BigDecimal("-10"));
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, List.of(p));
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(0, response.getTotalRewardPoints());

        }

        @Test
        @DisplayName("null amount throws RewardPointsException")
        void testNullAmountThrowsException() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(null);
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                when(rewardValidator.determineEffectiveDateRange(any(), any(), any()))
                                .thenReturn(new LocalDate[] { from, to });
                when(rewardPointsRepository.findByCustomerId(1))
                                .thenReturn(List.of(new RewardPoints()));
                when(rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(1, from, to)).thenReturn(List.of(p));

                RewardPointsException ex = assertThrows(RewardPointsException.class, () ->
                rewardService.calculateRewardPoints(1, 1, null, null));
                assertEquals("Purchase amount is null", ex.getMessage());
        }

        @Test
        @DisplayName("amount $50.01 earns 0 points (rounds to 0 excess)")
        void testAmountJustOver50() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(new BigDecimal("50.01"));
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, List.of(p));
                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(0, response.getTotalRewardPoints());

        }

        @Test
        @DisplayName("amount $100.01 earns 50 points (rounds to 0 excess over 100)")
        void testAmountJustOver100() {

                RewardPoints p = new RewardPoints();
                p.setCustomerId(1);
                p.setAmount(new BigDecimal("100.01"));
                p.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                p.setName("Rongali Ashok");
                p.setEmailId("rongali.ashok@gmail.com");
                LocalDate from = LocalDate.of(2026, 1, 1);
                LocalDate to = LocalDate.of(2026, 1, 31);
                setupValidatorAndRepository(1, from, to, List.of(p));

                RewardCalculationResponse response = rewardService.calculateRewardPoints(1, 1, null, null);
                assertEquals(50, response.getTotalRewardPoints());
        }
}
