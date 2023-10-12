package com.help.loan.distribute.service.billRec;

import com.help.loan.distribute.service.billRec.model.BillRecPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 结算记录service接口
 */
public interface BillRecService {

    Logger LOG = LoggerFactory.getLogger(BillRecService.class);


    /**
     *  根据结算日期查询结算记录
     * @param billDate 结算日期
     * @return BillRecPO
     */
    BillRecPO getBillRecByBillDate(String billDate);

    /**
     * 更新结算记录
     * @param billRec BillRecPO
     */
    void updateBillRec(BillRecPO billRec);

    /**
     * 增加新结算记录
     * @param billRec BillRecPO
     */
    void addBillRec(BillRecPO billRec);




}
