package com.crm.service.org;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.esign.ESignService;
import com.crm.service.esign.model.OrgESignPO;
import com.crm.service.org.dao.OrgPOMapper;
import com.crm.service.org.dao.OrgRegisterMapper;
import com.crm.service.org.model.OrgPO;
import com.crm.service.org.model.OrgRegisterPO;
import com.crm.util.ListUtil;
import com.ec.v2.utlis.Md5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构service实现类
 * 获取机构信息方法
 *
 * getOrgByDomain2(String domain2)
 *
 * getOrgById(Long id)
 *
 */
@Service
public class OrgServiceImpl implements OrgService {

    @Autowired
    OrgPOMapper orgPOMapper;

    @Autowired
    OrgRegisterMapper registerMapper;

    @Autowired
    CustomerService customerService;

    @Autowired
    CacheConfigService cacheConfigService;

    @Autowired
    ESignService eSignService;

    @Override
    public OrgPO getOrgByDomain2(String domain2) {
        if(StringUtils.isBlank(domain2)){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"您未授权，请联系客服！");
        }
        OrgPO org = orgPOMapper.selectOrgByDomain2(domain2);
        if(null == org || org.getStatus() != 1){
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"您未授权，请联系客服！");
        }
        return org;
    }

    @Override
    public OrgPO getOrgById(Long id) {
        if(null == id || id <= 0)
            return null;
        OrgPO orgPO = orgPOMapper.selectOrgById(id);
        if(null == orgPO){
            return orgPO;
        }
        orgPO.setOrgESign(eSignService.getOrgESign(id));
        return orgPO;
    }

    @Override
    public OrgPO addOrg(OrgPO orgPO) {
        orgPOMapper.insertOrg(orgPO);
        OrgESignPO orgESignPO = orgPO.getOrgESign();
        if(null == orgESignPO){
            return orgPO;
        }
        orgESignPO.setOrgId(orgPO.getId());
        return orgPO;
    }

    @Transactional
    @Override
    public void updateOrg(OrgPO orgPO) {
        if(null == orgPO || null == orgPO.getId()){
            return;
        }
        orgPO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        orgPOMapper.updateOrg(orgPO);
        OrgESignPO orgESignPO = orgPO.getOrgESign();
        if(null == orgESignPO){
            return;
        }
        orgESignPO.setOrgId(orgPO.getId());
        orgESignPO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        eSignService.updateOrgESign(orgESignPO);
    }

    @Transactional
    @Override
    public void addRegister(OrgRegisterPO registerPO) {
        if(null == registerPO || null == registerPO.getOrgId() || registerPO.getOrgId() <=0 || StringUtils.isBlank(registerPO.getMobile())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"机构id和客户手机必填");
        }
        if(null == orgPOMapper.selectOrgById(registerPO.getOrgId())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"机构不存在,非法请求");
        }
        List<OrgRegisterPO> list = registerMapper.selectRegister(registerPO.getOrgId(),registerPO.getMobile());
        if(!ListUtil.isEmpty(list)){
            return;
        }
        registerMapper.insertRegister(registerPO);

        CustomerPO customerPO = customerService.getCustomerByMd5(registerPO.getOrgId(), Md5Util.encryptMd5(registerPO.getMobile()));
        if(null == customerPO){
            return;
        }


        String processStr = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,registerPO.getOrgId().toString());
        Byte process = cacheConfigService.parseCustomerProcess(processStr,"上门");
        if(null != process && customerPO.getProgress() < process){
            CustomerPO po = new CustomerPO();
            po.setId(customerPO.getId());
            po.setProgress(process);
            customerService.updateCustomer(po);
        }




    }
}
