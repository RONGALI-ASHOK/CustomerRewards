package com.infy.rewardpoints.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.infy.rewardpoints.dto.RewardPointsDTO;
import com.infy.rewardpoints.exception.RewardPointsException;

public interface RewardService {
    public String calculateRewardPoints(String emailId, Integer noOfMonths, LocalDate fromDate ,LocalDate toDate) throws RewardPointsException;

    public CompletableFuture<List<RewardPointsDTO>> getPurchaseDetails(String emailId) throws RewardPointsException;
}
