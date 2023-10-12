package com.crm.service.label;

import com.crm.service.label.model.LabelPO;

import java.util.List;

public interface LabelService {

    List<LabelPO> selectAllLabel(Long orgId);

    void addLabel(LabelPO labelPO);

    void delLabel(Long id);

}
