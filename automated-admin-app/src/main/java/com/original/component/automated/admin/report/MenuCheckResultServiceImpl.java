package com.original.component.automated.admin.report;

import com.original.component.automated.admin.api.MenuCheckResultCQRS;
import com.original.component.automated.admin.dto.data.MenuCheckResultCO;
import com.original.component.automated.admin.report.entity.MenuCheckResult;
import com.original.framework.command.AbstractCommand;
import com.original.framework.data.AbstractCQRS;
import com.original.framework.dto.Query;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;

@Component
public class MenuCheckResultServiceImpl extends AbstractCQRS<MenuCheckResultCO, MenuCheckResult, String> implements MenuCheckResultCQRS {

    public MenuCheckResultServiceImpl(PagingAndSortingRepository<MenuCheckResult, String> repository, CacheManager cacheManager) {
        super(repository, cacheManager);
    }

    @Override
    public void beforeSave(AbstractCommand<MenuCheckResultCO, String> abstractCommand) throws Exception {

    }

    @Override
    public void afterSave(AbstractCommand<MenuCheckResultCO, String> abstractCommand, MenuCheckResult entity) throws Exception {

    }

    @Override
    public void beforeDelete(AbstractCommand<MenuCheckResultCO, String> abstractCommand) throws Exception {

    }

    @Override
    public void afterDelete(AbstractCommand<MenuCheckResultCO, String> abstractCommand) throws Exception {

    }

    @Override
    public Specification<MenuCheckResult> getSpecification(Query query) {
        return null;
    }
}
