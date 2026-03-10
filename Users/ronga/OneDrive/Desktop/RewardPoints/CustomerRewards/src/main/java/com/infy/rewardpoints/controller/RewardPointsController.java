package com.infy.rewardpoints.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.rewardpoints.dto.CustomerDetailsDTO;
import com.infy.rewardpoints.dto.WrapperDTO;
import com.infy.rewardpoints.exception.RewardPointsException;
import com.infy.rewardpoints.service.RewardService;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/rewards")
@Validated
public class RewardPointsController {

    @Autowired
    private RewardService rewardService;

    @GetMapping("/points")
    public ResponseEntity<WrapperDTO> calculateRewardPoints(
            @RequestParam(required = true) Integer customerId,

            @RequestParam(required = false) @Min(value = 1, message = "Number of months must be atleast 1") @Max(value = 3, message = "Number of months must be at most 3") Integer noOfMonths,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate)
            throws RewardPointsException {
        WrapperDTO points = rewardService.calculateRewardPoints(customerId, noOfMonths, fromDate, toDate);
        return new ResponseEntity<>(points, HttpStatus.OK);
    }

    @GetMapping("/purchase-details")
    public CompletableFuture<ResponseEntity<List<CustomerDetailsDTO>>> getPurchaseDetails(
            @RequestParam Integer customerId)
            throws RewardPointsException {

        return rewardService.getPurchaseDetails(customerId).thenApply(data -> new ResponseEntity<>(data, HttpStatus.OK));

    }

}
