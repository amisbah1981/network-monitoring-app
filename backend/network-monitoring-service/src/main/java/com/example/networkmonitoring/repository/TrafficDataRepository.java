package com.example.networkmonitoring.repository;

import com.example.networkmonitoring.model.TrafficData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficDataRepository extends JpaRepository<TrafficData, Long> {
}
