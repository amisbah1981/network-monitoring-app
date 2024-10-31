package com.example.batchMicroservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficDataTrainingRepository extends JpaRepository<TrafficDataTraining, Long> {
}
