package com.crm.service.dial;

import com.crm.service.dial.model.CustomerVoicePO;

import java.util.List;

public interface VoiceService {

    /**
     * 获客客户通话录音
     * @param orgId 机构id
     * @param customerPhone 客户电话
     * @return List<CustomerVoicePO>
     */
    List<CustomerVoicePO> getCustomerVoice(Long orgId, String customerPhone);

}
