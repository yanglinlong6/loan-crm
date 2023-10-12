package com.loan.cps.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.loan.cps.dao.LenderDao;
import com.loan.cps.dao.UserApplyRecordMapper;
import com.loan.cps.entity.LenderPO;
import com.loan.cps.entity.UserApplyRecordPO;


/**
 * 用户申请记录service接口实现
 */
@Service
public class UserApplyRecordServiceImpl implements UserApplyRecordService {
	
    private static final Logger log = LoggerFactory.getLogger(UserApplyRecordServiceImpl.class);
    
    @Autowired
    private LenderDao lenderDao;
    
    @Autowired
    private UserApplyRecordMapper userApplyRecordMapper;

    @Transactional
    @Override
    public boolean addApplyRecord(UserApplyRecordPO applyRecord) {
        if (null == applyRecord || StringUtils.isEmpty(applyRecord.getLenderId())) {
            return false;
        }
        // 判断产品是否存在，不存在则是非法请求
        LenderPO lender = lenderDao.selectByLenderId(applyRecord.getLenderId());
        if (null == lender) {
            return false;
        }

        //撞库去重，并联登
        // 生成申请记录id
        applyRecord.setApplyId(this.generateApplyId());
        applyRecord.setCompanyId(lender.getCompanyId());
        //保存申请记录
        userApplyRecordMapper.insertApplyRecord(applyRecord);
        
        applyRecord.setRedirectUrl(lender.getRedirectUrl());
        
        return true;
    }

    @Override
    public UserApplyRecordPO getByApplyId(String applyId) {
        if (StringUtils.isEmpty(applyId)) {
            return null;
        }
        return userApplyRecordMapper.selectByApplyId(applyId);
    }


    @Override
    public Integer getApplyNumByLenderId(String lenderId) {
        if (org.apache.commons.lang3.StringUtils.isBlank(lenderId)) {
            return 0;
        }
        Integer applyNum = userApplyRecordMapper.selectApplyNumByLenderId(lenderId);
        if (null == applyNum) {
            return 0;
        }
        return applyNum;
    }

}
