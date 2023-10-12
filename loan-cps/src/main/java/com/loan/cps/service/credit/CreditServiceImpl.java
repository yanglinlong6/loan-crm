package com.loan.cps.service.credit;

import com.loan.cps.common.CPSConstant;
import com.loan.cps.common.R;
import com.loan.cps.dao.CreditUserDao;
import com.loan.cps.dao.UserAptitudeDao;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.service.UserAptitudeService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditServiceImpl implements CreditService {

    private static final Logger LOG = LoggerFactory.getLogger(CreditServiceImpl.class);

    @Autowired
    private CreditUserDao creditUserDao;

    @Autowired
    private UserAptitudeDao dao;

    @Autowired
    private UserAptitudeService userAptitudeService;

    @Override
    public synchronized R addCreditUser(CreditUserPO po) {
        if(null == po || StringUtils.isBlank(po.getMobile())){
            return R.fail("【信用逾期】请填写手机号码");
        }
        if(StringUtils.isBlank(po.getName())){
            return R.fail("【信用逾期】请填写姓名");
        }
        if(StringUtils.isBlank(po.getAmount())){
            return R.fail("【信用逾期】请输入逾期金额");
        }
        if(StringUtils.isBlank(po.getOverdue())){
            return R.fail("【信用逾期】请输入逾期时间");
        }
        if(StringUtils.isBlank(po.getChannel())){
            return R.fail("【信用逾期】缺少渠道参数");
        }
        UserAptitudePO byMobile = dao.getByMobileByCreateBy(po.getMobile(),CPSConstant.CreateBy.CREDIT);
        if(null != byMobile){
            return R.ok("您已提交，请勿重复提交！");
        }
        UserAptitudePO userAptitudePO = new UserAptitudePO();
        userAptitudePO.setName(po.getName());
        userAptitudePO.setMobile(po.getMobile());
        userAptitudePO.setAge(po.getAge());
        userAptitudePO.setGender(po.getSex().intValue());
        userAptitudePO.setLoanAmount(po.getAmount());
        userAptitudePO.setCity(po.getCity());
        userAptitudePO.setHouse(0);
        userAptitudePO.setCar(0);
        userAptitudePO.setGetwayIncome(0);
        userAptitudePO.setOverdue(po.getOverdue());
        userAptitudePO.setLevel(4);
        userAptitudePO.setPublicFund(po.getLoanType());
        userAptitudePO.setChannel(po.getChannel());
        userAptitudePO.setCreateBy(CPSConstant.CreateBy.CREDIT);
        userAptitudeService.addCredit(userAptitudePO);
        return R.ok();
    }

    @Override
    public R addCreditUser2(CreditUserPO po) {
        if(null == po || StringUtils.isBlank(po.getMobile())){
            return R.fail("【信用逾期】请填写手机号码");
        }
        if(StringUtils.isBlank(po.getName())){
            return R.fail("【信用逾期】请填写姓名");
        }
//        if(StringUtils.isBlank(po.getAmount())){
//            return R.fail("【信用逾期】请输入逾期金额");
//        }
//        if(StringUtils.isBlank(po.getOverdue())){
//            return R.fail("【信用逾期】请输入逾期时间");
//        }
//        if(StringUtils.isBlank(po.getChannel())){
//            return R.fail("【信用逾期】缺少渠道参数");
//        }
        UserAptitudePO byMobile = dao.getByMobileByCreateBy(po.getMobile(),CPSConstant.CreateBy.CREDIT);
        if(null != byMobile){
            return R.ok();
        }
        UserAptitudePO userAptitudePO = new UserAptitudePO();
        userAptitudePO.setName(po.getName());
        userAptitudePO.setMobile(po.getMobile());
        userAptitudePO.setAge(po.getAge());
        userAptitudePO.setGender(po.getSex().intValue());
        userAptitudePO.setLoanAmount(po.getAmount());
        userAptitudePO.setCity(po.getCity());
        userAptitudePO.setPublicFund(po.getLoanType());
        userAptitudePO.setHouse(0);
        userAptitudePO.setCar(0);
        userAptitudePO.setGetwayIncome(0);
        userAptitudePO.setOverdue(po.getOverdue());
        userAptitudePO.setLevel(4);
        userAptitudePO.setChannel(po.getChannel());
        userAptitudePO.setCallTime(po.getCallTime());
        userAptitudePO.setCreateBy(CPSConstant.CreateBy.CREDIT);
        userAptitudeService.addCredit(userAptitudePO);
        return R.ok();
    }

    @Override
    public R updateCreditUser(CreditUserPO creditUserPO) {
        return null;
    }

    @Override
    public boolean sendCreditUser(CreditUserPO creditUserPO) {
        return false;
    }

    @Override
    public List<CreditUserPO> getCreditUserList(Integer count, Byte status) {
        return null;
    }
}
