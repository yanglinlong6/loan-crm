package com.loan.cps.service.accounting;

import com.loan.cps.common.R;

import java.util.List;

public interface AccountingService {

    R addAccountingUser(AccountingUserPO accountingUserPO);

    void updateAccountingUserById(AccountingUserPO accountingUserPO);

    /**
     * 发送客户
     * @param po
     * @return
     */
    public String send(AccountingUserPO po);

    /**
     * 获取财会客户列表
     * @param count 获取的数量
     * @param status 状态
     * @return List<AccountingUserPO>
     */
    List<AccountingUserPO> getAccountUserList(int count,Byte status);

}
