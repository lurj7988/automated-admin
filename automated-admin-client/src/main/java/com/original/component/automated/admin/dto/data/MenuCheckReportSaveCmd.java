package com.original.component.automated.admin.dto.data;

import com.original.framework.command.AbstractCommand;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MenuCheckReportSaveCmd extends AbstractCommand<MenuCheckReportCO, String> {

    public MenuCheckReportSaveCmd(MenuCheckReportCO clientObject) {
        super(clientObject);
    }
}