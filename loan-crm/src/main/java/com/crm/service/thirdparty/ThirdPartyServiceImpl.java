package com.crm.service.thirdparty;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.cache.model.CacheConfigPO;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.org.OrgService;
import com.crm.service.org.model.OrgPO;
import com.crm.service.thirdparty.dao.OrgThirdPartyMapper;
import com.crm.service.thirdparty.model.OrgThirdPartyPO;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 第三方合作机构service类
 */
@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {

    @Autowired
    OrgThirdPartyMapper orgThirdPartyMapper;

    @Autowired
    OrgService orgService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CacheConfigService cacheConfigService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Override
    public void getThirdPartyOrgByPage(PageBO<OrgThirdPartyPO> pageBO) {
        OrgThirdPartyPO paramObject = pageBO.getParamObject();
        if(null == paramObject){
            paramObject = new OrgThirdPartyPO();
        }
        paramObject.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        pageBO.setParamObject(paramObject);
        List<OrgThirdPartyPO> dataList = orgThirdPartyMapper.selectThirdPartyOrgByPage(pageBO);
        pageBO.setDataList(dataList);
        int totalCount = orgThirdPartyMapper.selectThirdPartyOrgByPageCount(pageBO);
        pageBO.setTotalCount(totalCount);
        if(totalCount == 0){
            pageBO.setPageCount(0);
        }else if(totalCount%pageBO.getSize() == 0){
            pageBO.setPageCount(totalCount/pageBO.getSize());
        }else
            pageBO.setPageCount(totalCount/pageBO.getSize()+1);

        pageBO.setParamObject(null);
    }

    @Transactional
    @Override
    public void addThirdPartyOrg(OrgThirdPartyPO po) {
        if(null == po)
            return;
        if(StringUtils.isBlank(po.getName()) || StringUtils.isBlank(po.getNickname()) || StringUtils.isBlank(po.getOrgCode())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"创建第三方合作机构失败:公司名称,简称,企业信用编码[必填]");
        }
        if(StringUtils.isBlank(po.getAdminName()) || StringUtils.isBlank(po.getAdminPhone())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"创建第三方合作机构失败:公司管理员姓名和手机号[必填]");
        }

        if(null == po.getProductId() || StringUtils.isBlank(po.getProductName())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"创建第三方合作机构失败:请选择产品");
        }

        if(StringUtils.isBlank(po.getCity())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"创建第三方合作机构失败:请选择城市");
        }

        // 创建机构
        OrgPO org = new OrgPO();
        org.setAutomatic(po.getAuto());
        org.setDial(CrmConstant.Org.Dial.Y);
        org.setStatus(CrmConstant.Org.Status.START);
        org.setName(po.getName());
        org.setNickname(po.getNickname());
        org.setAdress(po.getAddress());
        org.setDomain2("");
        orgService.addOrg(org);

        // 创建管理员
        OrgEmployeePO employee = new OrgEmployeePO();
        employee.setOrgId(org.getId());
        employee.setShopId(0L);
        employee.setTeamId(0L);
        employee.setName(po.getAdminName());
        employee.setPhone(po.getAdminPhone());
        employee.setStatus(CrmConstant.Employee.Status.YES);
        employee.setCount(0);
        employee.setLogin(CrmConstant.Employee.Login.Y);
        employee.setReceive(CrmConstant.Employee.Receive.NO);
        employee.setRoleId(CrmConstant.Role.ADMIN);
        employee.setRoleName(CrmConstant.Role.ADMIN_NAME);
        employee.setWechat(po.getAdminPhone());
        employee.setCreateBy(LoginUtil.getLoginEmployee().getName());
        employeeService.addEmployee(employee);

        // 初始化配置
        List<CacheConfigPO> configList = cacheConfigService.getConfig(1L);
        if(!ListUtil.isEmpty(configList)){
            for(CacheConfigPO config : configList){
                config.setKey(org.getId().toString());
                if("media".equals(config.getField())){
                    String value= config.getValue();
                    if(StringUtils.isBlank(value)){
                        config.setValue(LoginUtil.getLoginEmployee().getOrg().getNickname());
                    }else
                        config.setValue(config.getValue()+","+LoginUtil.getLoginEmployee().getOrg().getNickname());
                }
                cacheConfigService.addConfig(config);
                stringRedisTemplate.opsForHash().put(config.getField(),config.getKey(),config.getValue());
            }
        }

        po.setOrgId(org.getId());
        po.setBelongOrgId(LoginUtil.getLoginEmployee().getOrgId());

        orgThirdPartyMapper.insertThirdPartyOrg(po);

    }


    @Transactional
    @Override
    public void updateThirdPartyOrg(OrgThirdPartyPO po) {
        orgThirdPartyMapper.updateThirdPartyOrgById(po);
    }

    @Override
    public List<OrgThirdPartyPO> selectAllThirdPartyOrg(Long orgId, String city, Long productId) {
        return orgThirdPartyMapper.selectThirdPartyOrg(orgId,city,productId);
    }
}
