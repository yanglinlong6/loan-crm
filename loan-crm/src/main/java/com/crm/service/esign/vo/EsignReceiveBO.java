package com.crm.service.esign.vo;

import com.crm.util.JSONUtil;
import lombok.Getter;
import lombok.Setter;

/**
 * e签宝回调对象,例如:
 * {
 * 	"action": "SIGN_FLOW_UPDATE",
 * 	"flowId": "11111113a466442abbce094c9368ac7c",
 * 	"accountId": "22XXXe2a",
 * 	"authorizedAccountId": "33XXXe3a",
 * 	"signTime": "2019-07-24 19:33:06",
 * 	"order": 1,
 * 	"signResult": 2,
 * 	"thirdOrderNo": "cust0001",
 * 	"resultDescription": "签署完成",
 * 	"timestamp": 1563967986960,
 * 	"thirdPartyUserId": "A34006"
 * }
 *
 *
 */
@Setter
@Getter
public class EsignReceiveBO {

    /**
     * SIGN_FLOW_UPDATE - 签署人签署完成
     * SIGN_FLOW_FINISH - 流程结束
     * SIGN_DOC_EXPIRE_REMIND - 流程文件过期前提醒
     * SIGN_DOC_EXPIRE - 流程文件过期
     * BATCH _ ADD _ WATERMARK _ REMIND - 文件添加数字水印完成
     * FEEDBACK_SIGNERINFO - 签署人申请修改身份信息
     * PROCESS_HANDOVER - 经办人转交签署任务
     * WILL_FINISH - 意愿认证完成
     * PARTICIPANT_MARKREAD - 	签署人已读
     * SEAL_AUDIT - 印章审核
     */
    private String action;

    /**
     * 流程id
     */
    private String flowId;

    private String accountId; //签署人的accountId

    private String authorizedAccountId;//签约主体的账号id（个人/企业）；如签署人自身签署，则返回签署人账号id；如签署人代机构签署，则返回机构账号id 。

    private String signTime; // 签署时间或拒签时间 格式：yyyy-MM-dd HH:mm:ss

    private Integer order; //  签署人的签署顺序

    private Integer signResult; // 签署结果: 2-签署完成  3-失败  4-拒签

    private String thirdOrderNo; // 本次签署任务对应指定的第三方业务流水号id，当存在多个第三方业务流水号id时，返回多个，并逗号隔开该参数取值设置签署区的时候设置的thirdOrderNo参数

    private String resultDescription; // 拒签或失败时，附加的原因描述

    private Long timestamp; // 时间戳

    /**
     * 本次签署任务中对应的签署账号唯一标识，和创建当前签署账号时所传入的thirdPartyUserId值一致
     * 注：当签署人代机构签署时，返回的thirdPartyUserId为当前签署人在创建账号时，传入的thirdPartyUserId值。
     */
    private String thirdPartyUserId;

    @Override
    public String toString() {
        return JSONUtil.toJSONString(this);
    }
}
