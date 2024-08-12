package com.original.component.automated.admin.dto.data;

import com.original.framework.clientobject.AbstractClientObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Size;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuCheckResultCO extends AbstractClientObject<String> {

    @Size(max = 50)
    private String reportGuid;

    @Size(max = 50)
    private String code;

    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String url;

    @Size(max = 50)
    private String error;
}
