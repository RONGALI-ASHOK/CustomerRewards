package com.infy.rewardpoints.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="rewardPoints")
public class RewardPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="serialNumber")
    private Integer serialNumber;

    @Column(name="customerId")
    private Integer customerId;

    @Column(name="emailId")
    private String emailId;

    @Column(name="name")
    private String name;

    @Column(name="dateOfPurchase")
    private LocalDate dateOfPurchase;
    
    @Column(name="amount")
    private Integer amount;


}
