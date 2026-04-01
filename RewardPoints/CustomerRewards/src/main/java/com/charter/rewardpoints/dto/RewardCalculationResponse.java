package com.charter.rewardpoints.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({ "customerDetails", "monthlyRewardPoints", "totalRewardPoints" })
@Data
@Schema(description = "details of a customer's reward points calculation response")
public class RewardCalculationResponse {

    @Schema(description = "customer details")
    private CustomerDetails customerDetails;

    @Schema(description = "List of monthly reward points details for the customer")
    private List<MonthlyRewardPoints> monthlyRewardPoints;

    @Schema(description = "Total reward points for the customer")
    private Integer totalRewardPoints;

    @Schema(description = "List of purchase details of a customer")
    private List<CustomerPurchaseDetails> customerPurchaseDetails;
}
