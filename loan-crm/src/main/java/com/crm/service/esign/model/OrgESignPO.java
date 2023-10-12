package com.crm.service.esign.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * 机构e签宝配置对象
 */
@Getter
@Setter
public class OrgESignPO extends BasePO {

    private Long orgId;// 机构id

    private String appid;

    private String secret;

    private Byte auto;// 是否自动签署

    private String accountId; // 签约账户id

    private String orgAccountId; // 签约机构账户id

    private String orgNumber; // 机构营业执照编码/信用编码

    private Integer posPage;// 签章签在第几页

    private Float x;//签章x轴位置,单位mm

    private Float y;//签章y轴位置,单位mm

    private String noticeUrl;// 签约回调地址

    private String redirectUrl;// 签约成功后的重定向地址


    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}