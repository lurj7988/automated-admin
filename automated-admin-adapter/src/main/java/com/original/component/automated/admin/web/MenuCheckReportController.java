package com.original.component.automated.admin.web;

import com.original.component.automated.admin.api.MenuCheckReportCQRS;
import com.original.component.automated.admin.dto.data.MenuCheckReportCO;
import com.original.component.automated.admin.dto.data.MenuCheckReportSaveCmd;
import com.original.framework.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@Tag(name = "菜单扫描 - 报告")
@RestController
@RequestMapping("/automated/menucheck")
@Validated
public class MenuCheckReportController {

    @Resource
    MenuCheckReportCQRS menuCheckReportCQRS;

    @PostMapping("/create")
    @Operation(summary = "新增报告", description = "用于【菜单扫描】")
    public Response create(@Valid @RequestBody MenuCheckReportCO menuCheckReportCO) {
        MenuCheckReportSaveCmd cmd = new MenuCheckReportSaveCmd(menuCheckReportCO);
        return menuCheckReportCQRS.save(cmd);
    }
}
