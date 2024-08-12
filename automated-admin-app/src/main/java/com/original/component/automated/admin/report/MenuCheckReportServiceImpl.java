package com.original.component.automated.admin.report;

import com.original.component.automated.admin.api.MenuCheckReportCQRS;
import com.original.component.automated.admin.api.MenuCheckResultCQRS;
import com.original.component.automated.admin.dto.data.MenuCheckReportCO;
import com.original.component.automated.admin.dto.data.MenuCheckResultCO;
import com.original.component.automated.admin.dto.data.MenuCheckResultSaveCmd;
import com.original.component.automated.admin.report.entity.MenuCheckReport;
import com.original.component.automated.admin.report.repository.MenuCheckReportRepository;
import com.original.framework.command.AbstractCommand;
import com.original.framework.data.AbstractCQRS;
import com.original.framework.dto.Query;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MenuCheckReportServiceImpl extends AbstractCQRS<MenuCheckReportCO, MenuCheckReport, String> implements MenuCheckReportCQRS {

    @Resource
    MenuCheckResultCQRS menuCheckResultCQRS;

    public MenuCheckReportServiceImpl(MenuCheckReportRepository repository, CacheManager cacheManager) {
        super(repository, cacheManager);
    }

    @Override
    public void beforeSave(AbstractCommand<MenuCheckReportCO, String> abstractCommand) throws Exception {

    }

    @Override
    public void afterSave(AbstractCommand<MenuCheckReportCO, String> abstractCommand, MenuCheckReport saved) throws Exception {
        List<MenuCheckResultCO> menuCheckResultCOS = abstractCommand.getClientObject().getMenuCheckResultCOS();
        for (MenuCheckResultCO menuCheckResultCO : menuCheckResultCOS) {
            menuCheckResultCO.setReportGuid(saved.getId());
            MenuCheckResultSaveCmd cmd = new MenuCheckResultSaveCmd(menuCheckResultCO);
            menuCheckResultCQRS.save(cmd);
        }
    }

    @Override
    public void beforeDelete(AbstractCommand<MenuCheckReportCO, String> abstractCommand) throws Exception {

    }

    @Override
    public void afterDelete(AbstractCommand<MenuCheckReportCO, String> abstractCommand) throws Exception {

    }

    @Override
    public Specification<MenuCheckReport> getSpecification(Query query) {
        return null;
    }
}
