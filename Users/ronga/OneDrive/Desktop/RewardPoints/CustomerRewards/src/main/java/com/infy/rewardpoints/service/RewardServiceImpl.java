package com.infy.rewardpoints.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infy.rewardpoints.dto.RewardPointsDTO;
import com.infy.rewardpoints.entity.RewardPoints;
import com.infy.rewardpoints.exception.RewardPointsException;
import com.infy.rewardpoints.repository.RewardPointsRepository;

@Service(value = "rewardService")
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardPointsRepository rewardPointsRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public String calculateRewardPoints(String emailId, Integer noOfMonths, LocalDate fromDate, LocalDate toDate)
            throws RewardPointsException {

        // Here I'm reorganizing the dates to ensure that the values are consistent and
        // valid for the query.
        LocalDate effectiveTo;
        LocalDate effectiveFrom;
        effectiveTo = (toDate != null) ? toDate : LocalDate.now();
        if (fromDate != null) {
            effectiveFrom = fromDate;
        } else {
            int months = (noOfMonths != null) ? noOfMonths : 3;
            effectiveFrom = effectiveTo.minusMonths(months);
        }
        List<RewardPoints> purchaseDetails = rewardPointsRepository.findByEmailIdAndDateOfPurchaseBetween(emailId,
                effectiveFrom, effectiveTo);

        if (purchaseDetails.isEmpty()) {
            throw new RewardPointsException("No purchase details found for the Customer.");
        }

        HashMap<YearMonth, Integer> monthlyPoints = new HashMap<>();
        Integer totalRewardPoints = 0;
        for (RewardPoints rewardPoints : purchaseDetails) {
            Integer price = rewardPoints.getAmount();
            Integer points = (price > 100) ? (int) ((price - 100) * 2 + 50) : (price > 50) ? (int) (price - 50) : 0;
            YearMonth month = YearMonth.from(rewardPoints.getDateOfPurchase());
            if (!monthlyPoints.containsKey(month)) {
                monthlyPoints.put(month, points);
            } else {
                monthlyPoints.put(month, monthlyPoints.get(month) + points);
            }
            System.out.println(rewardPoints.getSerialNumber() + " " + rewardPoints.getEmailId() + " "
                    + rewardPoints.getName() + " " + rewardPoints.getDateOfPurchase() + " " + rewardPoints.getAmount());
            totalRewardPoints += points;
        }

        String result = "";
        for (Map.Entry<YearMonth, Integer> entry : monthlyPoints.entrySet()) {
            String monthName = entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            result += monthName + ": " + entry.getValue() + " points\n";
        }
        return emailId + "\n" + result + "Total: " + totalRewardPoints + " points";
    }

    @Async
    @Override
    public CompletableFuture<List<RewardPointsDTO>> getPurchaseDetails(String emailId) throws RewardPointsException {

        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<RewardPoints> purchaseDetails = rewardPointsRepository.findByEmailId(emailId);
        if (purchaseDetails.isEmpty()) {
            throw new RewardPointsException("No purchase details found for the Customer.");
        }

        List<RewardPointsDTO> rewardPointsDTOs = modelMapper.map(purchaseDetails,
                new TypeToken<List<RewardPointsDTO>>() {
                }.getType());
        return CompletableFuture.completedFuture(rewardPointsDTOs);
    }
}