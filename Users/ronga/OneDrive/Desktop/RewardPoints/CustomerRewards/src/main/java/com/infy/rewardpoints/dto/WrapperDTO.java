package com.infy.rewardpoints.dto;

import java.util.List;

import lombok.Data;

@Data
public class WrapperDTO {
    private RewardPointsDTO rewardPoints;
    private List<CustomerDetailsDTO> customerDetails;
    
}
