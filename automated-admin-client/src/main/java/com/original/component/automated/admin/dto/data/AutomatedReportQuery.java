package com.original.component.automated.admin.dto.data;

import com.original.framework.dto.AbstractQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AutomatedReportQuery extends AbstractQuery<String> {

    private String reportGuid;
}
