package com.original.component.automated.admin.report.repository;

import com.original.component.automated.admin.report.entity.MenuCheckResult;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MenuCheckResultRepository extends PagingAndSortingRepository<MenuCheckResult, String>, JpaSpecificationExecutor<MenuCheckResult> {
}
