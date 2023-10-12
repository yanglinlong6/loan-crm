package com.crm.service.employee.model;

import com.crm.common.BasePO;
import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrgEmployeePO extends BasePO {

    private Long orgId;

    private Long shopId;

    private Long teamId;

    private String name;

    private String phone;// 手机号码，用于登录，

    private String password;// 密码

    private String logo;// 公司logo图片地址

    private Byte status;// 员工状态：1-在职，0-离职

    private Integer count;// '每日接收新数据数量

    private Byte login; // '是否能允许登陆：1-允许，0-禁止(默认)',

    private Byte receive; // 是否接收数据：1-接收，0-不接收（默认）

    private Long roleId;// 岗位角色id，0-表示超级管理员,拥有全部权限，  其他需要社会权限

    private String roleName;// 角色名称，也就是岗位

    private Long channelId;

    private String wechat; // 员工微信号码

    private boolean updateAction;//标记是否更新员工行为

    private Integer distributeCount=0;

    private String accountId; // e签宝: account_id

    @Override
    public String toString() {
        if(null == this)
            return "OrgEmployeePO{}";
        return JSONUtil.toJSONString(this);
    }
}