package com.charter.rewardpoints.service;
import java.math.BigDecimal;
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
        Map<YearMonth, Double> monthlyPoints = calculateMonthlyPoints(purchaseDetails);
        Double totalRewardPoints = calculateTotalPoints(monthlyPoints);

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

    private Map<YearMonth, Double> calculateMonthlyPoints(List<RewardPoints> purchaseDetails) {

        Map<YearMonth, Double> monthlyPoints = new HashMap<>();

        for (RewardPoints rewardPoints : purchaseDetails) {

            double points = calculatePointsForPurchase(rewardPoints.getAmount());
            YearMonth ym = YearMonth.from(rewardPoints.getDateOfPurchase());
            monthlyPoints.merge(ym, points, Double::sum);
        }

        return monthlyPoints;
    }
    
    private static final BigDecimal FIFTY = new BigDecimal("50");
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final BigDecimal TWO = new BigDecimal("2");

    private Double calculatePointsForPurchase(BigDecimal amount) {
        if (amount == null) {
            throw new RewardPointsException("Purchase amount is null");
        }
        if (amount.compareTo(ONE_HUNDRED) > 0) {
            return amount.subtract(ONE_HUNDRED)
                .multiply(TWO)
                .add(FIFTY)
                .doubleValue();
        } else if (amount.compareTo(FIFTY) > 0) {
            return amount.subtract(FIFTY)
                .doubleValue();
        }
        return 0.0;
    }

    private Double calculateTotalPoints(Map<YearMonth, Double> monthlyPoints) {
        return monthlyPoints.values()
                .stream()
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private RewardCalculationResponse assembleResponse(List<RewardPoints> purchaseDetails,
            Map<YearMonth, Double> monthlyPoints, Double totalRewardPoints) {

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
            Map<YearMonth, Double> monthlyPoints) {

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
