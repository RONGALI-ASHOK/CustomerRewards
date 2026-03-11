package com.infy.rewardpoints.controller;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
import com.infy.rewardpoints.dto.CustomerDetailsDTO;
import com.infy.rewardpoints.dto.RewardPointsDTO;
import com.infy.rewardpoints.dto.WrapperDTO;
import com.infy.rewardpoints.exception.RewardPointsException;
import com.infy.rewardpoints.service.RewardService;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(RewardPointsController.class)
public class RewardPointsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private RewardService rewardService;

        private final String NO_PURCHASE_DETAILS_MESSAGE = "No purchase details found for the Customer.";
        private final String REQUEST_FAILED_MESSAGE = "Request failed";
        private final String VALIDATION_FAILED_MESSAGE = "Validation failed";

        @Test
        @DisplayName("GET /rewards/points - Valid: Returns reward points with all params provided")
        void testCalculateRewardPointsValidAllParams() throws Exception {

                RewardPointsDTO rewardPointsDTO = new RewardPointsDTO();
                rewardPointsDTO.setCustomerId(1);
                rewardPointsDTO.setName("Rongali Ashok");
                rewardPointsDTO.setEmailId("ashok.rongali@gmail.com");
                rewardPointsDTO.setTotalPoints("250");

                LinkedHashMap<String, Integer> monthly = new LinkedHashMap<>();
                monthly.put("January", 150);
                monthly.put("February", 100);
                rewardPointsDTO.setMonthlyPoints(monthly);

                WrapperDTO mockResponse = new WrapperDTO();
                mockResponse.setRewardPointsDTO(rewardPointsDTO);
                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.any()))
                                .thenReturn(mockResponse);
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "2")
                                .param("fromDate", LocalDate.now().minusMonths(2).toString())
                                .param("toDate", LocalDate.now().toString())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.rewardPointsDTO.customerId",
                                                Matchers.is(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.rewardPointsDTO.name",
                                                Matchers.is("Rongali Ashok")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.rewardPointsDTO.totalPoints",
                                                Matchers.is("250")));

        }

        @Test
        @DisplayName("GET /rewards/points - Valid: Returns reward points with only customerId (optional params absent)")
        void testCalculateRewardPointsValidOnlyCustomerId() throws Exception {

                RewardPointsDTO rewardPointsDTO = new RewardPointsDTO();
                rewardPointsDTO.setCustomerId(1);
                rewardPointsDTO.setTotalPoints("90");
                rewardPointsDTO.setName("Rongali Ashok");
                rewardPointsDTO.setEmailId("ashok.rongali@gmail.com");

                LinkedHashMap<String, Integer> monthly = new LinkedHashMap<>();
                monthly.put("January", 50);
                monthly.put("February", 40);
                rewardPointsDTO.setMonthlyPoints(monthly);

                WrapperDTO mockResponse = new WrapperDTO();
                mockResponse.setRewardPointsDTO(rewardPointsDTO);
                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.anyInt(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull()))
                                .thenReturn(mockResponse);

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.rewardPointsDTO.customerId",
                                                Matchers.is(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.rewardPointsDTO.totalPoints",
                                                Matchers.is("90")));

        }

        @Test
        @DisplayName("GET /rewards/points - Valid: Returns reward points with customerDetailsDTO list populated")
        void testCalculateRewardPointsValidWithCustomerDetails() throws Exception {

                CustomerDetailsDTO detail = new CustomerDetailsDTO();
                detail.setCustomerId(1);
                detail.setName("Rongali Ashok");
                detail.setEmailId("ashok.rongali@gmail.com");
                detail.setDateOfPurchase(LocalDate.of(2026, 1, 15));
                detail.setAmount(200);

                WrapperDTO mockResponse = new WrapperDTO();
                mockResponse.setCustomerDetailsDTO(Collections.singletonList(detail));
                Mockito.when(rewardService.calculateRewardPoints(

                                Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.any()))
                                .thenReturn(mockResponse);

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetailsDTO", Matchers.hasSize(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetailsDTO[0].customerId",
                                                Matchers.is(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetailsDTO[0].name",
                                                Matchers.is("Rongali Ashok")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.customerDetailsDTO[0].amount",
                                                Matchers.is(200)));

        }

        @Test
        @DisplayName("GET /rewards/points - Invalid: noOfMonths exceeds max (> 3) triggers 400 Bad Request")
        void testCalculateRewardPointsInvalidMonthsExceedsMax() throws Exception {

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "5")
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
        @DisplayName("GET /rewards/points - Invalid: Service throws RewardPointsException returns 404 with error body")
        void testCalculateRewardPointsServiceException() throws Exception {

                Mockito.when(rewardService.calculateRewardPoints(
                                Mockito.anyInt(), Mockito.any(), Mockito.any(), Mockito.any()))
                                .thenThrow(new RewardPointsException(NO_PURCHASE_DETAILS_MESSAGE));

                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/points")
                                .param("customerId", "1")
                                .param("noOfMonths", "2")
                                .param("fromDate", LocalDate.now().minusMonths(2).toString())
                                .param("toDate", LocalDate.now().toString())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.is(REQUEST_FAILED_MESSAGE)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]",
                                                Matchers.is(NO_PURCHASE_DETAILS_MESSAGE)));

        }

        @Test
        @DisplayName("GET /rewards/purchase-details - Valid: Returns list of purchase details for a customer")

        void testGetPurchaseDetailsValid() throws Exception {

                CustomerDetailsDTO detail1 = new CustomerDetailsDTO();
                detail1.setCustomerId(1);
                detail1.setName("Rongali Ashok");
                detail1.setEmailId("ashok.rongali@gmail.com");
                detail1.setDateOfPurchase(LocalDate.of(2026, 1, 10));
                detail1.setAmount(150);

                CustomerDetailsDTO detail2 = new CustomerDetailsDTO();
                detail2.setCustomerId(1);
                detail2.setName("Rongali Ashok");
                detail2.setEmailId("ashok.rongali@gmail.com");
                detail2.setDateOfPurchase(LocalDate.of(2026, 2, 20));
                detail2.setAmount(220);
                List<CustomerDetailsDTO> mockList = Arrays.asList(detail1, detail2);

                Mockito.when(rewardService.getPurchaseDetails(Mockito.anyInt()))
                                .thenReturn(CompletableFuture.completedFuture(mockList));

                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get("/rewards/purchase-details")
                                                .param("customerId", "1")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                                .andReturn();

                mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].customerId", Matchers.is(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", Matchers.is("Rongali Ashok")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount", Matchers.is(150)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[1].amount", Matchers.is(220)));

        }

        @Test
        @DisplayName("GET /rewards/purchase-details - Valid: Returns single purchase detail record")
        void testGetPurchaseDetailsValidSingleRecord() throws Exception {

                CustomerDetailsDTO detail = new CustomerDetailsDTO();
                detail.setCustomerId(2);
                detail.setName("Jane Smith");
                detail.setEmailId("jane.smith@example.com");
                detail.setDateOfPurchase(LocalDate.of(2026, 3, 5));
                detail.setAmount(300);

                Mockito.when(rewardService.getPurchaseDetails(Mockito.anyInt()))
                                .thenReturn(CompletableFuture.completedFuture(Collections.singletonList(detail)));

                MvcResult mvcResult = mockMvc
                                .perform(MockMvcRequestBuilders.get("/rewards/purchase-details")
                                                .param("customerId", "2")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.request().asyncStarted())
                                .andReturn();

                mockMvc.perform(MockMvcRequestBuilders.asyncDispatch(mvcResult))
                                .andExpect(MockMvcResultMatchers.status().isOk())
                                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].customerId", Matchers.is(2)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].emailId",
                                                Matchers.is("jane.smith@example.com")))
                                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount", Matchers.is(300)));

        }

        @Test
        @DisplayName("GET /rewards/purchase-details - Invalid: Service throws RewardPointsException returns 404 with error body")
        void testGetPurchaseDetailsServiceException() throws Exception {

                Mockito.when(rewardService.getPurchaseDetails(Mockito.anyInt()))
                                .thenThrow(new RewardPointsException(NO_PURCHASE_DETAILS_MESSAGE));
                mockMvc.perform(MockMvcRequestBuilders.get("/rewards/purchase-details")
                                .param("customerId", "99")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(MockMvcResultMatchers.status().isNotFound())
                                .andExpect(MockMvcResultMatchers.jsonPath("$.message",
                                                Matchers.is(REQUEST_FAILED_MESSAGE)))
                                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]",
                                                Matchers.is(NO_PURCHASE_DETAILS_MESSAGE)));
        }
}
