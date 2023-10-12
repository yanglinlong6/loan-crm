package com.crm.common;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class BasePO {

    protected Long id;

    protected String createBy;

    protected Date createDate;

    protected String updateBy;

    protected Date updateDate;
}
