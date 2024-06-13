package com.original.component.automated.admin.api;

import com.original.component.automated.admin.dto.data.AutomatedReportCO;
import com.original.component.automated.admin.dto.data.AutomatedReportQuery;
import com.original.framework.api.CQRS;
import com.original.framework.dto.SingleResponse;

public interface AutomatedReportCQRS extends CQRS<AutomatedReportCO, String> {

    SingleResponse<AutomatedReportCO> queryOneByReportGuid(AutomatedReportQuery query);
}
