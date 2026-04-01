package com.charter.rewardpoints.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import com.charter.rewardpoints.dto.CustomerDetails;
import com.charter.rewardpoints.dto.CustomerPurchaseDetails;
import com.charter.rewardpoints.dto.MonthlyRewardPoints;
import com.charter.rewardpoints.dto.RewardCalculationResponse;
import com.charter.rewardpoints.entity.RewardPoints;
import com.charter.rewardpoints.exception.RewardPointsException;
import com.charter.rewardpoints.repository.RewardPointsRepository;
import com.charter.rewardpoints.utility.RewardValidator;

@Service(value = "rewardService")
public class RewardServiceImpl implements RewardService {

    private final RewardValidator rewardValidator;
    private final RewardPointsRepository rewardPointsRepository;
    private final ModelMapper modelMapper;

    public RewardServiceImpl(RewardValidator rewardValidator, RewardPointsRepository rewardPointsRepository,
            ModelMapper modelMapper) {
        this.rewardValidator = rewardValidator;
        this.rewardPointsRepository = rewardPointsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RewardCalculationResponse calculateRewardPoints(Integer customerId, Integer noOfMonths, LocalDate fromDate, LocalDate toDate) {

        rewardValidator.validateInputParameters(noOfMonths, fromDate, toDate);
        LocalDate[] dateRange = rewardValidator.determineEffectiveDateRange(noOfMonths, fromDate, toDate);
        rewardValidator.validateDateRange(dateRange[0], dateRange[1]);

        return calculateMonthlyRewardPoints(customerId, dateRange[0], dateRange[1]);
    }

    private RewardCalculationResponse calculateMonthlyRewardPoints(Integer customerId, LocalDate effectiveFrom, LocalDate effectiveTo) {

        validateCustomerExists(customerId);
        List<RewardPoints> purchaseDetails = fetchPurchaseDetails(customerId, effectiveFrom, effectiveTo);
        Map<YearMonth, Integer> monthlyPoints = calculateMonthlyPoints(purchaseDetails);
        Integer totalRewardPoints = calculateTotalPoints(monthlyPoints);

        return assembleResponse(purchaseDetails, monthlyPoints, totalRewardPoints);
    }

    private void validateCustomerExists(Integer customerId) {
        if (rewardPointsRepository.findByCustomerId(customerId).isEmpty()) {
            throw new RewardPointsException("There is no customer with the given Id.");
        }
    }

    private List<RewardPoints> fetchPurchaseDetails(Integer customerId, LocalDate effectiveFrom,
            LocalDate effectiveTo) {

        List<RewardPoints> purchaseDetails = rewardPointsRepository
                .findByCustomerIdAndDateOfPurchaseBetween(customerId, effectiveFrom, effectiveTo);

        if (purchaseDetails.isEmpty()) {
            throw new RewardPointsException("No purchase details found for the Customer for the specified period.");
        }
        return purchaseDetails;
    }

    private Map<YearMonth, Integer> calculateMonthlyPoints(List<RewardPoints> purchaseDetails) {

        Map<YearMonth, Integer> monthlyPoints = new HashMap<>();

        for (RewardPoints rewardPoints : purchaseDetails) {

            int points = calculatePointsForPurchase(rewardPoints.getAmount());
            YearMonth ym = YearMonth.from(rewardPoints.getDateOfPurchase());
            monthlyPoints.merge(ym, points, Integer::sum);
        }

        return monthlyPoints;
    }

    private Integer calculatePointsForPurchase(BigDecimal amount) {
        if (amount == null) {
            throw new RewardPointsException("Purchase amount is null");
        }
        if (amount.compareTo(new BigDecimal("100")) > 0) {

            BigDecimal excessOver100 = amount.subtract(new BigDecimal("100")).setScale(0, RoundingMode.HALF_UP);
            return excessOver100.multiply(new BigDecimal("2")).add(new BigDecimal("50")).intValue();
        } else if (amount.compareTo(new BigDecimal("50")) > 0) {

            BigDecimal excessOver50 = amount.subtract(new BigDecimal("50")).setScale(0, RoundingMode.HALF_UP);
            return excessOver50.intValue();
        }
        return 0;
    }

    private Integer calculateTotalPoints(Map<YearMonth, Integer> monthlyPoints) {
        return monthlyPoints.values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    private RewardCalculationResponse assembleResponse(List<RewardPoints> purchaseDetails,
            Map<YearMonth, Integer> monthlyPoints, Integer totalRewardPoints) {

        List<MonthlyRewardPoints> monthlyRewardPointsList = convertToMonthlyRewardPointsList(monthlyPoints);
        List<CustomerPurchaseDetails> customerDetailsList = modelMapper.map(purchaseDetails,
                new TypeToken<List<CustomerPurchaseDetails>>() {
                }.getType());

        RewardPoints customer = purchaseDetails.get(0);
        CustomerDetails customerDetails = createCustomerDetails(customer);

        RewardCalculationResponse response = new RewardCalculationResponse();
        response.setMonthlyRewardPoints(monthlyRewardPointsList);
        response.setTotalRewardPoints(totalRewardPoints);
        response.setCustomerDetails(customerDetails);
        response.setCustomerPurchaseDetails(customerDetailsList);
        return response;
    }

    private List<MonthlyRewardPoints> convertToMonthlyRewardPointsList(
            Map<YearMonth, Integer> monthlyPoints) {

        List<MonthlyRewardPoints> monthlyRewardPointsList = new ArrayList<>();

        monthlyPoints.forEach((yearMonth, points) -> {
            MonthlyRewardPoints mrp = new MonthlyRewardPoints();
            mrp.setYear(yearMonth.getYear());
            mrp.setMonth(yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
            mrp.setPoints(points);
            monthlyRewardPointsList.add(mrp);
        });

        monthlyRewardPointsList.sort(Comparator
                .comparing(MonthlyRewardPoints::getYear)
                .thenComparing(m -> Month.valueOf(m.getMonth().toUpperCase())));

        return monthlyRewardPointsList;
    }

    private CustomerDetails createCustomerDetails(RewardPoints customer) {
        CustomerDetails details = new CustomerDetails();
        details.setCustomerId(customer.getCustomerId());
        details.setName(customer.getName());
        details.setEmailId(customer.getEmailId());
        return details;
    }

}
