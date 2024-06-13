package com.original.component.automated.admin.report.repository;

import com.original.component.automated.admin.report.entity.AutomatedReport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface AutomatedReportRepository extends PagingAndSortingRepository<AutomatedReport, String>, JpaSpecificationExecutor<AutomatedReport> {
}
