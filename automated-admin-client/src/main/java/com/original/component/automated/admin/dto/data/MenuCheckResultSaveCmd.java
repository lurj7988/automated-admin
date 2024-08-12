package com.original.component.automated.admin.dto.data;

import com.original.framework.command.AbstractCommand;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class MenuCheckResultSaveCmd extends AbstractCommand<MenuCheckResultCO, String> {

    public MenuCheckResultSaveCmd(MenuCheckResultCO clientObject) {
        super(clientObject);
    }
}