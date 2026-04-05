package com.charter.rewardpoints.controller;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.charter.rewardpoints.dto.CustomerDetails;
import com.charter.rewardpoints.dto.CustomerPurchaseDetails;
import com.charter.rewardpoints.dto.MonthlyRewardPoints;
import com.charter.rewardpoints.dto.RewardCalculationResponse;
import com.charter.rewardpoints.exception.RewardPointsException;
import com.charter.rewardpoints.service.RewardService;

@WebMvcTest(RewardPointsController.class)
@DisplayName("RewardPointsController Integration Tests")
public class RewardPointsIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RewardService rewardService;

        private final String NO_PURCHASE_DETAILS_MESSAGE = "No purchase details found for the Customer.";
        private final String REQUEST_FAILED_MESSAGE = "The request cannot be processed";
        private final String VALIDATION_FAILED_MESSAGE = "There is a constraint violation";

        private void stubSuccessResponse() {

                CustomerDetails customerDetails = new CustomerDetails();
                customerDetails.setCustomerId(1);
                customerDetails.setName("Rongali Ashok");
                customerDetails.setEmailId("rongali.ashok@gmail.com");
                MonthlyRewardPoints monthlyPoints = new MonthlyRewardPoints();
                monthlyPoints.setYear(2026);
                monthlyPoints.setMonth("January");
                monthlyPoints.setPoints(150.0);
                CustomerPurchaseDetails purchaseDetail = new CustomerPurchaseDetails();
                purchaseDetail.setDateOfPurchase(LocalDate.of(2026, 1, 15));
                purchaseDetail.setAmount(new java.math.BigDecimal("150"));
                RewardCalculationResponse mockResponse = new RewardCalculationResponse();
                mockResponse.setCustomerDetails(customerDetails);
                mockResponse.setMonthlyRewardPoints(Collections.singletonList(monthlyPoints));
                mockResponse.setTotalRewardPoints(150.0);
                mockResponse.setCustomerPurchaseDetails(Collections.singletonList(purchaseDetail));
                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.eq(1), Mockito.eq(1), Mockito.isNull(), Mockito.isNull()))
                                .thenReturn(mockResponse);
        }

        @Test
        @DisplayName("GET /rewards/points - returns 200 OK with JSON content type")
        void testReturnsOkStatusWithJsonContentType() throws Exception {

                stubSuccessResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("GET /rewards/points - returns correct customer details")
        void testReturnsCustomerDetails() throws Exception {

                stubSuccessResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetails.customerId",
                                                Matchers.is(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetails.name",
                                                Matchers.is("Rongali Ashok")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetails.emailId",
                                                Matchers.is("rongali.ashok@gmail.com")));
        }

        @Test
        @DisplayName("GET /rewards/points - returns correct total reward points")
        void testReturnsTotalRewardPoints() throws Exception {

                stubSuccessResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRewardPoints",
                                                Matchers.is(150.0)));
        }

        @Test
        @DisplayName("GET /rewards/points - returns correct monthly reward points breakdown")
        void testReturnsMonthlyRewardPoints() throws Exception {

                stubSuccessResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyRewardPoints", Matchers.hasSize(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyRewardPoints[0].month",
                                                Matchers.is("January")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyRewardPoints[0].points",
                                                Matchers.is(150.0)));
        }

        private void stubDateRangeResponse() {

                CustomerDetails customerDetails = new CustomerDetails();
                customerDetails.setCustomerId(1);
                customerDetails.setName("Rongali Ashok");
                customerDetails.setEmailId("rongali.ashok@gmail.com");
                List<MonthlyRewardPoints> monthlyPoints = new ArrayList<>();
                MonthlyRewardPoints jan = new MonthlyRewardPoints();
                jan.setYear(2026);
                jan.setMonth("January");
                jan.setPoints(150.0);
                monthlyPoints.add(jan);
                MonthlyRewardPoints feb = new MonthlyRewardPoints();
                feb.setYear(2026);
                feb.setMonth("February");
                feb.setPoints(50.0);
                monthlyPoints.add(feb);
                RewardCalculationResponse mockResponse = new RewardCalculationResponse();
                mockResponse.setCustomerDetails(customerDetails);
                mockResponse.setMonthlyRewardPoints(monthlyPoints);
                mockResponse.setTotalRewardPoints(200.0);
                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.eq(1), Mockito.isNull(),
                                Mockito.eq(LocalDate.of(2026, 1, 1)),
                                Mockito.eq(LocalDate.of(2026, 2, 28))))
                                .thenReturn(mockResponse);
        }

        @Test
        @DisplayName("GET /rewards/points - date range returns correct monthly breakdown count")
        void testDateRangeReturnsMonthlyBreakdownCount() throws Exception {

                stubDateRangeResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("fromDate", "2026-01-01")
                                .param("toDate", "2026-02-28")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.monthlyRewardPoints",
                                                Matchers.hasSize(2)));
        }

        @Test
        @DisplayName("GET /rewards/points - date range returns correct total reward points")
        void testDateRangeReturnsTotalRewardPoints() throws Exception {

                stubDateRangeResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("fromDate", "2026-01-01")
                                .param("toDate", "2026-02-28")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRewardPoints", Matchers.is(200.0)));
        }

        @Test
        @DisplayName("GET /rewards/points - Valid: Returns reward points with only customerId (defaults to 3 months)")
        void testCalculateRewardPointsDefaultMonths() throws Exception {

                CustomerDetails customerDetails = new CustomerDetails();
                customerDetails.setCustomerId(1);
                customerDetails.setName("Rongali Ashok");
                customerDetails.setEmailId("rongali.ashok@gmail.com");
                RewardCalculationResponse mockResponse = new RewardCalculationResponse();
                mockResponse.setCustomerDetails(customerDetails);
                mockResponse.setMonthlyRewardPoints(new ArrayList<>());
                mockResponse.setTotalRewardPoints(300.0);
                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.eq(1), Mockito.isNull(), Mockito.isNull(), Mockito.isNull()))
                                .thenReturn(mockResponse);
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.totalRewardPoints", Matchers.is(300.0)));
        }

        private void stubPurchaseDetailsResponse() {

                CustomerDetails customerDetails = new CustomerDetails();
                customerDetails.setCustomerId(1);
                customerDetails.setName("Rongali Ashok");
                customerDetails.setEmailId("rongali.ashok@gmail.com");
                CustomerPurchaseDetails purchase1 = new CustomerPurchaseDetails();
                purchase1.setDateOfPurchase(LocalDate.of(2026, 1, 15));
                purchase1.setAmount(new java.math.BigDecimal("150"));
                CustomerPurchaseDetails purchase2 = new CustomerPurchaseDetails();
                purchase2.setDateOfPurchase(LocalDate.of(2026, 2, 20));
                purchase2.setAmount(new java.math.BigDecimal("75"));
                RewardCalculationResponse mockResponse = new RewardCalculationResponse();
                mockResponse.setCustomerDetails(customerDetails);
                mockResponse.setCustomerPurchaseDetails(Arrays.asList(purchase1, purchase2));
                mockResponse.setMonthlyRewardPoints(new ArrayList<>());
                mockResponse.setTotalRewardPoints(175.0);
                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.eq(1), Mockito.eq(2), Mockito.isNull(), Mockito.isNull()))
                                .thenReturn(mockResponse);
        }

        @Test
        @DisplayName("GET /rewards/points - returns correct number of purchase details")
        void testReturnsPurchaseDetailsCount() throws Exception {

                stubPurchaseDetailsResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerPurchaseDetails",
                                                Matchers.hasSize(2)));
        }

        @Test
        @DisplayName("GET /rewards/points - returns correct purchase amounts")
        void testReturnsPurchaseAmounts() throws Exception {

                stubPurchaseDetailsResponse();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerPurchaseDetails[0].amount",
                                                Matchers.is(150)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerPurchaseDetails[1].amount",
                                                Matchers.is(75)));
        }

        @Test
        @DisplayName("GET /rewards/points - Invalid: noOfMonths exceeds max (> 12) triggers 400 Bad Request")
        void testCalculateRewardPointsInvalidMonthsExceedsMax() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "13")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.is(VALIDATION_FAILED_MESSAGE)));
        }

        @Test
        @DisplayName("GET /rewards/points - Invalid: noOfMonths below min (< 1) triggers 400 Bad Request")
        void testCalculateRewardPointsInvalidMonthsBelowMin() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "0")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.is(VALIDATION_FAILED_MESSAGE)));
        }

        @Test
        @DisplayName("GET /rewards/points - Invalid: Missing customerId triggers 400 Bad Request")
        void testCalculateRewardPointsMissingCustomerId() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("noOfMonths", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }

        private void stubServiceException() {

                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.eq(1), Mockito.eq(2), Mockito.isNull(), Mockito.isNull()))
                                .thenThrow(new RewardPointsException(NO_PURCHASE_DETAILS_MESSAGE));
        }

        @Test
        @DisplayName("GET /rewards/points - service exception returns 404 status")
        void testServiceExceptionReturns404() throws Exception {
                
                stubServiceException();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isNotFound());
        }
        
        @Test
        @DisplayName("GET /rewards/points - service exception returns correct error message")
        void testServiceExceptionReturnsErrorMessage() throws Exception {
        
                stubServiceException();
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "2")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.is(REQUEST_FAILED_MESSAGE)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]",
                                                Matchers.is(NO_PURCHASE_DETAILS_MESSAGE)));
        }
        
        @Test
        @DisplayName("GET /rewards/points - Error: Invalid date format returns 400")
        void testCalculateRewardPointsInvalidDateFormat() throws Exception {
        
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("fromDate", "invalid-date")
                                .param("toDate", "2026-02-28")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
}
