package com.cyber.playdetector.repository;

import com.cyber.playdetector.model.FraudResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudResultRepository extends JpaRepository<FraudResult, Long> {
}
