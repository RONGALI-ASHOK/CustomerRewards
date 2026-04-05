package com.charter.rewardpoints.controller;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.charter.rewardpoints.dto.RewardCalculationResponse;
import com.charter.rewardpoints.service.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/rewards")
@Validated
@Tag(name = "Reward Points API", description = "API for calculating reward points and fetching purchase details")
public class RewardPointsController {

        private final RewardService rewardService;
        public RewardPointsController(RewardService rewardService) {
                this.rewardService = rewardService;
        }

        @Operation(
                        summary = "Calculate Reward Points",
                        description = "Calculates the reward points for a given customer ID based on their purchase history over a specified number of months or date range.")
        @ApiResponses(value = {
                        @ApiResponse(
                                        responseCode = "200",
                                        description = "Successfully calculated reward points"
                        ),
                        @ApiResponse(
                                        responseCode = "400",
                                        description = "Validation failed — noOfMonths must be between 1 and 12"
                        ),
                        @ApiResponse(
                                        responseCode = "404",
                                        description = "No purchase details found for the customer for the specified period or no customer found with the given Id"
                        )
        })

        @GetMapping("/points")
        public ResponseEntity<RewardCalculationResponse> calculateRewardPoints(
                        @Parameter(
                                        description = "ID of the customer for whom to calculate reward points",
                                        required = true,
                                        example = "1")
                        @RequestParam(required = true)
                        Integer customerId,

                        @Parameter(
                                        description = "Number of months for which to calculate reward points (Min=1, Max=12)", required = false,
                                        example = "1")
                        @RequestParam(required = false)
                        @Min(value = 1, message = "Number of months must be atleast 1")
                        @Max(value = 12, message = "Number of months must be at most 12")
                        Integer noOfMonths,

                        @Parameter(
                                        description = "Start date for calculating reward points (format: yyyy-MM-dd)",
                                        required = false,
                                        example = "2026-01-01")
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                        LocalDate fromDate,

                        @Parameter(
                                        description = "End date for calculating reward points (format: yyyy-MM-dd)",
                                        required = false,
                                        example = "2026-03-11")
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                        LocalDate toDate) {

                RewardCalculationResponse points = rewardService.calculateRewardPoints(customerId, noOfMonths, fromDate, toDate);

                return new ResponseEntity<>(points, HttpStatus.OK);

        }
}