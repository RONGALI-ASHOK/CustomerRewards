package com.charter.rewardpoints.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({"customerId", "name", "emailId"})
@Data
@Schema(description = "details of a customer")
public class CustomerDetails {

    @Schema(description = "Unique ID of the customer", example = "1")
    private Integer customerId;

    @Schema(description = "Name of the customer", example = "Rongali Ashok")
    private String name;
    
    @Schema(description = "Email ID of the customer", example = "rongali.ashok@infy.com")
    private String emailId;
}
