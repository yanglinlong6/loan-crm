package com.help.loan.distribute.service.billRec;

import com.help.loan.distribute.service.billRec.dao.BillRecMapper;
import com.help.loan.distribute.service.billRec.model.BillRecPO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillRecServiceImpl implements BillRecService{


    @Autowired
    BillRecMapper billRecMapper;


    @Override
    public BillRecPO getBillRecByBillDate(String billDate) {
        if(StringUtils.isBlank(billDate))
            return null;
        return billRecMapper.selectBillRecByBillDate(billDate);
    }

    @Override
    public void updateBillRec(BillRecPO billRec) {
        if(null == billRec || StringUtils.isBlank(billRec.getBillDate()) || null == billRec.getStatus()){
            LOG.error("BillRecServiceImpl.updateBillRec 缺少结算日期参数:[{}],或者缺少结算状态参数:[{}]",billRec.getBillDate(),billRec.getStatus());
            return;
        }
        billRecMapper.updateBillRec(billRec);
    }

    @Override
    public void addBillRec(BillRecPO billRec) {
        if(null == billRec || StringUtils.isBlank(billRec.getBillDate()) || null == billRec.getStatus()){
            LOG.error("BillRecServiceImpl.addBillRec 缺少结算日期参数:[{}],或者缺少结算状态参数:[{}]",billRec.getBillDate(),billRec.getStatus());
            return;
        }
        billRecMapper.insertBillRec(billRec);
    }
}
