package com.help.loan.distribute.service.billRec.model;

import com.help.loan.distribute.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 结算记录
 */
@Setter
@Getter
public class BillRecPO extends BasePO {

    private String billDate;

    private Byte status;

    private Date createDate;

    private Date updateDate;


}