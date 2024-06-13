package com.original.component.automated.admin.dto.data;

import com.original.framework.command.AbstractCommand;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class AutomatedReportSaveCmd extends AbstractCommand<AutomatedReportCO, String> {

    public AutomatedReportSaveCmd(AutomatedReportCO clientObject) {
        super(clientObject);
    }
}