package com.infy.rewardpoints.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CustomerDetailsDTO {

    private String serialNumber;
    private Integer customerId;
    private String name;
    private String emailId;
    private LocalDate dateOfPurchase;
    private Integer amount;

}
