package com.infy.rewardpoints.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.infy.rewardpoints.entity.RewardPoints;

@Repository
public interface RewardPointsRepository extends CrudRepository<RewardPoints,Integer>{

    public List<RewardPoints> findByCustomerIdAndDateOfPurchaseBetween(Integer customerId, LocalDate fromDate, LocalDate toDate);

    public List<RewardPoints> findByCustomerId(Integer customerId);
}
