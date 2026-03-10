package com.infy.rewardpoints.dto;

import java.util.HashMap;

import lombok.Data;

@Data
public class RewardPointsDTO {
    private Integer customerId;
    private String name;
    private String emailId;
    private String totalPoints;
    private HashMap<String,Integer> monthlyPoints;
    

}
