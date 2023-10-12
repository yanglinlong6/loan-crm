package com.loan.cps.controller;

import com.loan.cps.common.R;
import com.loan.cps.service.accounting.AccountingService;
import com.loan.cps.service.accounting.AccountingServiceImpl;
import com.loan.cps.service.accounting.AccountingUserPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountingController {

    private static final Logger LOG = LoggerFactory.getLogger(AccountingController.class);

    @Autowired
    private AccountingService accountingService;


    /**
     * 财会接口
     * @param accounting
     * @return
     */
    @RequestMapping("/accounting/apply")
    @ResponseBody
    public R apply(@RequestBody() AccountingUserPO accounting){
        LOG.info("[财会]获客：{}",accounting);
        R  r = accountingService.addAccountingUser(accounting);
        return r;
    }

    @RequestMapping("/accounting/send")
    @ResponseBody
    public R send(){
        LOG.info("[财会]未发送成功定时任务");
        List<AccountingUserPO> list = accountingService.getAccountUserList(10, AccountingServiceImpl.FAIL);
        if(null == list || list.size()<=0){
            return R.ok();
        }
        for(AccountingUserPO userPO : list){
            accountingService.send(userPO);
        }
        return R.ok();
    }


}
