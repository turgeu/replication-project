package com.example.demo.repository;

import com.example.demo.model.ReplicationConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReplicationConfigRepository extends JpaRepository<ReplicationConfig, Integer> {
    List<ReplicationConfig> findByActiveTrue();
}
