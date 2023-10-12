package com.crm.service.label;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.login.LoginUtil;
import com.crm.service.label.dao.LabelMapper;
import com.crm.service.label.model.LabelPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelServiceImpl implements LabelService {


    @Autowired
    LabelMapper labelMapper;

    @Override
    public List<LabelPO> selectAllLabel(Long orgId) {
        if(null == orgId || orgId <= 0){
            return null;
        }
        return labelMapper.selectAllLabel(orgId);
    }

    @Override
    public void addLabel(LabelPO labelPO) {
        LabelPO po = labelMapper.selectLabel(LoginUtil.getLoginEmployee().getOrgId(),labelPO.getName());
        if(null != po){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"标签名称已存在!");
        }
        labelMapper.insertLabel(labelPO);
    }

    @Override
    public void delLabel(Long id) {
        labelMapper.deleteLabel(id);
    }
}
