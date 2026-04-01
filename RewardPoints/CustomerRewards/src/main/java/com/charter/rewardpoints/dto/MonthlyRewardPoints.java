package com.charter.rewardpoints.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({"year", "month", "points"})
@Data
@Schema(description = "details of a customer's reward points for a specific month and year")
public class MonthlyRewardPoints {

    @Schema(description = "Year of the purchase", example = "2024")
    private Integer year;

    @Schema(description = "Month of the purchase", example = "January")
    private String month;
    
    @Schema(description = "Reward points earned for the month", example = "50")
    private Integer points;
}
