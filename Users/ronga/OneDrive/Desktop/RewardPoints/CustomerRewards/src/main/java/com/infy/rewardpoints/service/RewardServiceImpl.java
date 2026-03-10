package com.infy.rewardpoints.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.infy.rewardpoints.dto.CustomerDetailsDTO;
import com.infy.rewardpoints.dto.RewardPointsDTO;
import com.infy.rewardpoints.dto.WrapperDTO;
import com.infy.rewardpoints.entity.RewardPoints;
import com.infy.rewardpoints.exception.RewardPointsException;
import com.infy.rewardpoints.repository.RewardPointsRepository;

@Service(value = "rewardService")
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardPointsRepository rewardPointsRepository;

   
    RewardPointsDTO rewardPointsDTO = new RewardPointsDTO();

   
     WrapperDTO wrapperDTO = new WrapperDTO();

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public WrapperDTO calculateRewardPoints(Integer customerId, Integer noOfMonths, LocalDate fromDate, LocalDate toDate)
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
        if(effectiveFrom.isAfter(effectiveTo) || effectiveFrom.isAfter(LocalDate.now()) || effectiveTo.isAfter(LocalDate.now())) {
            throw new RewardPointsException("Enter the Date in correct format");
        }
        List<RewardPoints> purchaseDetails = rewardPointsRepository.findByCustomerIdAndDateOfPurchaseBetween(customerId,
                effectiveFrom, effectiveTo);

        if (purchaseDetails.isEmpty()) {
            throw new RewardPointsException("No purchase details found for the Customer.");
        }
        RewardPoints customer = purchaseDetails.get(0);
        HashMap<String, Integer> monthlyPoints = new HashMap<>();
        Integer totalRewardPoints = 0;
        for (RewardPoints rewardPoints : purchaseDetails) {
            Integer price = rewardPoints.getAmount();
            Integer points = (price > 100) ? (int) ((price - 100) * 2 + 50) : (price > 50) ? (int) (price - 50) : 0;
            String month = YearMonth.from(rewardPoints.getDateOfPurchase()).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            if (!monthlyPoints.containsKey(month)) {
                monthlyPoints.put(month, points);
            } else {
                monthlyPoints.put(month, monthlyPoints.get(month) + points);
            }
           
            totalRewardPoints += points;
        }
        rewardPointsDTO.setName(customer.getName());
        rewardPointsDTO.setCustomerId(customerId);
        rewardPointsDTO.setEmailId(customer.getEmailId());
        rewardPointsDTO.setMonthlyPoints(monthlyPoints);
        rewardPointsDTO.setTotalPoints("Total Reward Points"+" : "+String.valueOf(totalRewardPoints));
        List<CustomerDetailsDTO> customerDetails = modelMapper.map(purchaseDetails,
                new TypeToken<List<CustomerDetailsDTO>>() {}.getType());
        
        wrapperDTO.setRewardPoints(rewardPointsDTO);
        wrapperDTO.setCustomerDetails(customerDetails);
        return wrapperDTO;
    }

    @Async
    @Override
    public CompletableFuture<List<CustomerDetailsDTO>> getPurchaseDetails(Integer customerId) throws RewardPointsException {

        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<RewardPoints> purchaseDetails = rewardPointsRepository.findByCustomerId(customerId);
        if (purchaseDetails.isEmpty()) {
            throw new RewardPointsException("No purchase details found for the Customer.");
        }

        List<CustomerDetailsDTO> rewardPointsDTOs = modelMapper.map(purchaseDetails,
                new TypeToken<List<CustomerDetailsDTO>>() {
                }.getType());
        return CompletableFuture.completedFuture(rewardPointsDTOs);
    }
}