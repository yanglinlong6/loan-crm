package com.crm.service.api;

import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.service.api.model.ImportBO;
import com.crm.service.customer.dao.CustomerFieldMapper;
import com.crm.service.customer.model.CustomerFieldPO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.util.JSONUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface ApiService {

    /**
     * api导入客户
     * @param importBO ImportBO
     */
    void importCustomer(ImportBO importBO);

    /**
     *  财会业务接入客户,实时分配
     * @param importBO 接入的客户信息
     * @return OrgEmployeePO 返回接入的客户信息分配给哪个员工
     */
    OrgEmployeePO importAccountingCustomer(ImportBO importBO);

    /**
     * 机构自动分配
     * @param orgId 机构id
     */
    void distributeCustomer(Long orgId);

    /**
     * 将客户直接分配给员工,不判断是否登陆
     * @param customer
     * @return
     */
    OrgEmployeePO distributeCustomer(CustomerPO customer);


    default void checkParam(CustomerPO po, CustomerFieldMapper customerFieldMapper){
        if(StringUtils.isBlank(po.getMedia()))
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少媒体");
        if(StringUtils.isBlank(po.getCity()))
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少城市");
        // 如果是全国客户，则从备注中解析城市
        po.setCity(parseCity(po));
//        List<CustomerFieldPO> fieldList = customerFieldMapper.selectAllField(po.getOrgId());
//        if(ListUtil.isEmpty(fieldList))
//            return;
//        JSONObject json = JSONUtil.toJSON(po);
//        for(CustomerFieldPO field : fieldList){
//            String key = field.getFieldCode().replaceAll("_","");
//            if(json.containsKey(key) && !field.getValue().contains(json.getString(key))){
//                throw  new CrmException(CrmConstant.ResultCode.FAIL,key+"参数错误");
//            }
//        }
    }

    default String parseCity(CustomerPO po){
        if(StringUtils.isNotBlank(po.getCity()) && po.getCity().endsWith("市"))
            return po.getCity();
        if(!"全国".equals(po.getCity())){
            return po.getCity();
        }
        String remark = po.getRemark();
        if(StringUtils.isBlank(remark)){
            return po.getCity();
        }
        String[] array = remark.replace("，",",").split(",");
        if(null == array || array.length < 2) {
            return po.getCity();
        }
        String city = array[0];
        if(StringUtils.isBlank(city) || !city.endsWith("市"))
            return po.getCity();
        return city;
    }
}
