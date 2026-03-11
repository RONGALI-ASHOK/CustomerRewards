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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Autowired
    private RewardService rewardService;

    @Operation( 
        summary = "Calculate Reward Points", 
        description = "Calculates reward points for a customer based on their purchases within a specified date range or number of months.")

    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully calculated reward points",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = WrapperDTO.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation failed — noOfMonths must be between 1 and 3",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"Validation failed\",\"errors\":[\"Number of months must be at most 3\"]}"))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No purchase details found for the customer",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"Request failed\",\"errors\":[\"No purchase details found for the Customer.\"]}"))
        )
    })
    @GetMapping("/points")
    public ResponseEntity<WrapperDTO> calculateRewardPoints(

            @Parameter( 
                description = "ID of the customer for whom to calculate reward points", 
                required = true,
                example = "1")
            @RequestParam(required = true) 
            Integer customerId,

            @Parameter(
                description="Number of months for which to calculate reward points (Min=1, Max=3)", required = false, 
                example = "1")
            @RequestParam(required = false) 
            @Min(value = 1, message = "Number of months must be atleast 1") 
            @Max(value = 3, message = "Number of months must be at most 3") 
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
            LocalDate toDate) throws RewardPointsException {

        WrapperDTO points = rewardService.calculateRewardPoints(customerId, noOfMonths, fromDate, toDate);
        return new ResponseEntity<>(points, HttpStatus.OK);
    }

    @Operation(
        summary = "Get Purchase Details", 
        description = "Fetches the purchase details for a given customer ID.")

     @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved purchase details",
            content = @Content(mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = CustomerDetailsDTO.class)))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No purchase details found for the customer",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"message\":\"Request failed\",\"errors\":[\"No purchase details found for the Customer.\"]}"))
        )

    })
    @GetMapping("/purchase-details")
    public CompletableFuture<ResponseEntity<List<CustomerDetailsDTO>>> getPurchaseDetails(
            @Parameter(
                description = "ID of the customer for whom to fetch purchase details", 
                required = true, 
                example = "1")
            @RequestParam Integer customerId)
            throws RewardPointsException {

        return rewardService.getPurchaseDetails(customerId).thenApply(data -> new ResponseEntity<>(data, HttpStatus.OK));

    }

}
