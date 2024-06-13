package com.original.component.automated.admin.report.repository;

import com.original.component.automated.admin.report.entity.AutomatedReport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface AutomatedReportRepository extends PagingAndSortingRepository<AutomatedReport, String>, JpaSpecificationExecutor<AutomatedReport> {

    Optional<AutomatedReport> findByReportGuid(String reportGuid);
}
