package com.original.component.automated.admin.web;

import com.original.component.automated.admin.api.AutomatedReportCQRS;
import com.original.component.automated.admin.dto.data.AutomatedReportCO;
import com.original.component.automated.admin.dto.data.AutomatedReportQuery;
import com.original.framework.dto.SingleResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class WebViewController {

    @Resource
    AutomatedReportCQRS automatedReportCQRS;

    @RequestMapping(value = "/RanorexReport", method = RequestMethod.GET)
    public String root(Model model, HttpServletRequest req) {
        AutomatedReportQuery automatedReportQuery = new AutomatedReportQuery();
        automatedReportQuery.setReportGuid(req.getParameter("ReportGuid"));
        SingleResponse<AutomatedReportCO> response = automatedReportCQRS.queryOneByReportGuid(automatedReportQuery);
        model.addAttribute("ReportGuid", response.getResult().getReportPath());
        return "RanorexReport";
    }
}
