package com.crm.service.org.model;


import com.crm.common.BasePO;
import com.crm.service.esign.model.OrgESignPO;
import lombok.Getter;
import lombok.Setter;

/**
 * 机构对象
 */
@Setter
@Getter
public class OrgPO extends BasePO {

    private String name; // 机构全程

    private String nickname;// 机构简称

    private String domain2;// 配置访问的二级域名

    private String adress; // 机构办公地址  必须真实

    private String logo; // 机构的logo

    private Byte status; // 机构状态: 0-暂停,1-启用

    private String loginMobile;

    private String loginName;

    private String roleName;

    private Byte automatic;// 是否自动分配:1-是，0-否

    private Byte dial; // 是否开启拨号：0-关闭，1-开启

    private String accountId; // e签宝:机构账号id

    private OrgESignPO orgESign; // 机构e签宝配置信息

    private String qrcode; // 机构二维码地址


}