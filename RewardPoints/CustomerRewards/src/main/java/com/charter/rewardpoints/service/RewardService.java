package com.charter.rewardpoints.service;
import java.time.LocalDate;
import com.charter.rewardpoints.dto.RewardCalculationResponse;

public interface RewardService {
    
    public RewardCalculationResponse calculateRewardPoints(Integer customerId, Integer noOfMonths, LocalDate fromDate ,LocalDate toDate);
}
