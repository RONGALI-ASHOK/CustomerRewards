package com.infy.rewardpoints.dto;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@JsonPropertyOrder({"serialNumber", "customerId", "name", "emailId", "dateOfPurchase", "amount"})
@Data
@Schema(description = "details of a single purchase by acustomer")
public class CustomerDetailsDTO {

    @Schema(description = "Serial number of the purchase record", example = "1")
    private String serialNumber;

    @Schema(description = "ID of the customer", example = "1")
    private Integer customerId;

    @Schema(description = "Name of the customer", example = "Rongali Ashok")
    private String name;

    @Schema(description = "Email ID of the customer", example = "rongali.ashok@infy.com")
    private String emailId;

    @Schema(description = "Date of the purchase", example = "2024-01-15")
    private LocalDate dateOfPurchase;
    
    @Schema(description = "Amount of the purchase", example = "100")
    private Integer amount;

}
