package com.tpa.logsmanagementservice.repository;

import com.tpa.logsmanagementservice.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogManagementRepository extends JpaRepository<Log, Long> {

}
