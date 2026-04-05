package com.charter.rewardpoints.dto;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({"dateOfPurchase", "amount"})
@Data
@Schema(description = "details of a single purchase by acustomer")
public class CustomerPurchaseDetails {
    
    @Schema(description = "Date of the purchase", example = "2024-01-15")
    private LocalDate dateOfPurchase;
    
    @Schema(description = "Amount of the purchase", example = "100.00")
    private BigDecimal amount;

}
