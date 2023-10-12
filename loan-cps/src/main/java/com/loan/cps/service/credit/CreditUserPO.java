package com.loan.cps.service.credit;

import com.loan.cps.common.BasePO;
import com.loan.cps.common.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 信用逾期业务对象
 */
@Setter
@Getter
public class CreditUserPO extends BasePO {

    private String name;

    private String mobile;

    private Integer age=0;

    private Byte sex=0;

    private String amount;

    private String overdue; // 1-3月，4-6月，7-9月，10-12月，12月以上

    private String loanType;//信用逾期：借款类型

    private String msgCode;// 短信验证码

    private String city;

    private Byte status=0;

    private String channel;

    private String ucid;

    private String clueId;

    private String telphone;

    private String remark_dict;

    private String form_remark;

    private String callTime;// 随时回访、上午回访、下午回访、晚上回访


    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}