package com.daofen.crm.service.counselor.model;

import com.daofen.crm.base.BasePO;
import com.daofen.crm.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CompanyCounselorPO extends BasePO {
    @NotBlank(message = "手机号码必填")
    private String mobile; // 顾问账户手机号码，也即是账户

    @NotBlank(message = "姓名必填")
    private String name;// 顾问账户姓名

    private Long companyId; // 机构id

    private Long shopId; // 门店id，账户可能是分公司管理员，没有归到门店

    private Long teamId; // 团队id，账户可能是管理员，没有归到团队

    private Byte status; //顾问状态(0-下线，1-上线）,默认为：0--下线(忙碌)

    @NotNull(message = "角色为必选")
    private Long roleId; // 角色id

    private Integer allocationCount; // 分发量


    private Byte open = 0; //是否开启接收(0-关闭，1-开启),默认为：0-关闭，不接受数据

    @NotNull(message = "账户类型必选")
    @Min(value = 1,message = "账户类型必选(1-内部，2-外部,外部账号用户媒体方使用)")
    @Max(value = 2,message = "账户类型必选(1-内部，2-外部,外部账号用户媒体方使用)")
    private Byte type;//顾问账户类型(1-内部，2-外部,外部账号用户媒体方使用)

    private String media;//如果是外部账号，用于查询媒体数据转化，则该字段不能为空，多个媒体以“,”号隔开

    private String remark;//备注

    private String fromExten;//席位号



    @Override
    public String toString() {
        return JSONUtil.toString(this);
    }
}