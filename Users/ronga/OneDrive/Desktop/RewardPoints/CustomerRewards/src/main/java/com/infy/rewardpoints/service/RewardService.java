package com.infy.rewardpoints.service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.infy.rewardpoints.dto.CustomerDetailsDTO;
import com.infy.rewardpoints.dto.WrapperDTO;
import com.infy.rewardpoints.exception.RewardPointsException;

public interface RewardService {
    public WrapperDTO calculateRewardPoints(Integer customerId, Integer noOfMonths, LocalDate fromDate ,LocalDate toDate) throws RewardPointsException;

    public CompletableFuture<List<CustomerDetailsDTO>> getPurchaseDetails(Integer customerId) throws RewardPointsException;
}
