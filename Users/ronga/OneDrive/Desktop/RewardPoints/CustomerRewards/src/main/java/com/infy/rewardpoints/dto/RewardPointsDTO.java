package com.infy.rewardpoints.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RewardPointsDTO {

    private String serialNumber;
    private String name;
    private String emailId;
    private LocalDate dateOfPurchase;
    private Integer amount;

}
