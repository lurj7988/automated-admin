package com.original.component.automated.admin.report.entity;

import com.original.framework.data.entity.IDEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "automated_report")
public class AutomatedReport extends IDEntity {
}
