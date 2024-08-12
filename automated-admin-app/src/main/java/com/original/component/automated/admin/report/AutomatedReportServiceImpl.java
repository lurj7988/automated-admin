package com.original.component.automated.admin.report;

import com.original.component.automated.admin.api.AutomatedReportCQRS;
import com.original.component.automated.admin.dto.data.AutomatedReportCO;
import com.original.component.automated.admin.dto.data.AutomatedReportQuery;
import com.original.component.automated.admin.report.entity.AutomatedReport;
import com.original.component.automated.admin.report.repository.AutomatedReportRepository;
import com.original.framework.command.AbstractCommand;
import com.original.framework.data.AbstractCQRS;
import com.original.framework.dto.Query;
import com.original.framework.dto.SingleResponse;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AutomatedReportServiceImpl extends AbstractCQRS<AutomatedReportCO, AutomatedReport, String> implements AutomatedReportCQRS {

    public AutomatedReportServiceImpl(AutomatedReportRepository repository, CacheManager cacheManager) {
        super(repository, cacheManager);
    }

    @Override
    public void beforeSave(AbstractCommand<AutomatedReportCO, String> abstractCommand) throws Exception {

    }

    @Override
    public void afterSave(AbstractCommand<AutomatedReportCO, String> abstractCommand, AutomatedReport saved) throws Exception {

    }

    @Override
    public void beforeDelete(AbstractCommand<AutomatedReportCO, String> abstractCommand) throws Exception {

    }

    @Override
    public void afterDelete(AbstractCommand<AutomatedReportCO, String> abstractCommand) throws Exception {

    }

    @Override
    public Specification<AutomatedReport> getSpecification(Query query) {
        return null;
    }

    @Override
    public SingleResponse<AutomatedReportCO> queryOneByReportGuid(AutomatedReportQuery query) {
        AutomatedReportRepository automatedReportRepository = (AutomatedReportRepository) this.getRepository();
        Optional<AutomatedReport> automatedReports = automatedReportRepository.findByReportGuid(query.getReportGuid());
        AutomatedReport automatedReport = automatedReports.orElse(new AutomatedReport());
        return SingleResponse.of(convertEntity2CO(automatedReport));
    }
}
