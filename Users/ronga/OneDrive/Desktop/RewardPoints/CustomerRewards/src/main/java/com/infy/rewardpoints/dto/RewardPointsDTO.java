package com.infy.rewardpoints.dto;

import java.util.LinkedHashMap;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({"customerId", "name", "emailId", "totalPoints", "monthlyPoints"})
@Data
@Schema(description = "Reward points details for a customer, including total points and monthly breakdown")
public class RewardPointsDTO {

    @Schema(description = "Unique ID of the customer", example = "1")
    private Integer customerId;

    @Schema(description = "Name of the customer", example = "Rongali Ashok")
    private String name;

    @Schema(description = "Email ID of the customer", example = "rongali.ashok@gmail.com")
    private String emailId;

    @Schema(description = "Total reward points for the customer", example = "150")
    private String totalPoints;
    
    @Schema(
        description = "Monthly breakdown of reward points", 
        example = "{\"January\": 50, \"February\": 75, \"March\": 25}")
    private LinkedHashMap<String,Integer> monthlyPoints;
    

}
