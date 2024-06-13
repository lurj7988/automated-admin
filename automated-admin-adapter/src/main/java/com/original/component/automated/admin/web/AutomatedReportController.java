package com.original.component.automated.admin.web;

import com.original.component.automated.admin.api.AutomatedReportCQRS;
import com.original.component.automated.admin.dto.data.AutomatedReportCO;
import com.original.component.automated.admin.dto.data.AutomatedReportSaveCmd;
import com.original.framework.dto.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Tag(name = "自动化 - 报告")
@RestController
@RequestMapping("/automated/report")
@Validated
public class AutomatedReportController {

    @Resource
    AutomatedReportCQRS automatedReportCQRS;

    @PostMapping("/create")
    @Operation(summary = "新增报告", description = "新增报告接口")
    public Response createAutomatedReport(@Valid @RequestBody AutomatedReportCO automatedReportCO) {
        AutomatedReportSaveCmd cmd = new AutomatedReportSaveCmd(automatedReportCO);
        return automatedReportCQRS.save(cmd);
    }


    @RequestMapping(value = "/RanorexReport", method = RequestMethod.GET)
    public String root(Model model, HttpServletRequest req) {
        model.addAttribute("ReportGuid", req.getParameter("ReportGuid"));
        return "RanorexReport";
    }
}
