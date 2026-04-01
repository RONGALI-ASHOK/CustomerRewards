package com.charter.rewardpoints.utility;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.charter.rewardpoints.exception.RewardPointsException;

@Component

public class RewardValidator {

    public void validateInputParameters(Integer noOfMonths, LocalDate fromDate, LocalDate toDate){
        if (noOfMonths != null && (fromDate != null || toDate != null)) {
            throw new RewardPointsException(
                    "Please provide either noOfMonths or fromDate and toDate, not both.");
        }
        if ((fromDate == null && toDate != null) || (fromDate != null && toDate == null)) {
            throw new RewardPointsException("Please provide both fromDate and toDate.");
        }
    }

    public LocalDate[] determineEffectiveDateRange(Integer noOfMonths, LocalDate fromDate, LocalDate toDate) {
        LocalDate effectiveTo = (toDate != null) ? toDate : LocalDate.now();
        LocalDate effectiveFrom = (fromDate != null) ? fromDate : LocalDate.now().minusMonths((noOfMonths != null) ? noOfMonths : 3);
        return new LocalDate[] { effectiveFrom, effectiveTo };
    }

    public void validateDateRange(LocalDate effectiveFrom, LocalDate effectiveTo) {
        if (effectiveFrom.isAfter(effectiveTo)) {
            throw new RewardPointsException("The toDate must be after the fromDate.");
        }
    }
}
