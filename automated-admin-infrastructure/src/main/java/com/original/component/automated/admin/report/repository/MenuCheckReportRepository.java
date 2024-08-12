package com.original.component.automated.admin.report.repository;

import com.original.component.automated.admin.report.entity.MenuCheckReport;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MenuCheckReportRepository extends PagingAndSortingRepository<MenuCheckReport, String>, JpaSpecificationExecutor<MenuCheckReport> {
}
