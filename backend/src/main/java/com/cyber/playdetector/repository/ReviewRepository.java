package com.cyber.playdetector.repository;

import com.cyber.playdetector.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
