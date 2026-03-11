package com.infy.rewardpoints.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({ "rewardPointsDTO", "customerDetailsDTO" })
@Data
@Schema(description = "Wrapper DTO for containing reward points and customer details")
public class WrapperDTO {

    @Schema(description = "Reward points details for the customer")
    private RewardPointsDTO rewardPointsDTO;
    
    @Schema(description = "List of purchase details of a customer")
    private List<CustomerDetailsDTO> customerDetailsDTO;
}
