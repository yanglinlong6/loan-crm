package com.crm.controller.customer;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.CustomerBO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.employee.EmployeeMsgService;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.EmployeeMsgPO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.org.OrgService;
import com.crm.service.org.model.OrgPO;
import com.crm.service.role.model.RolePO;
import com.crm.service.sms.SmsApi;
import com.crm.service.sms.WDSms;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import com.crm.util.excel.XLSXCovertCSVReader;
import com.ec.v2.utlis.Md5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * 公共池接口
 */
@RestController
public class PublicCustomerController {


    private static final Logger LOG = LoggerFactory.getLogger(PublicCustomerController.class);

    @Autowired
    CustomerService customerService;

    @Autowired
    EmployeeMsgService employeeMsgService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    OrgService orgService;


    /**
     * 公共池分页
     * @param pageBO
     * @return
     */
    @PostMapping("/customer/public/page")
    @ResponseBody
    public ResultVO publicPool(@RequestBody(required = false) PageBO<CustomerBO> pageBO){
        if(null == pageBO){
            return ResultVO.fail("公共池分页失败，未传参数",pageBO);
        }
        CustomerBO bo = pageBO.getParamObject();
        if(null == bo){
            bo = new CustomerBO();
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        RolePO role = employee.getRole();
//        if(!JudgeUtil.in(role.getType(), CrmConstant.Role.Type.ADMIN,CrmConstant.Role.Type.SHOP,CrmConstant.Role.Type.TEAM)){
//            return ResultVO.success("公共池分页成功",pageBO);
//        }
//        if(CrmConstant.Role.Type.SHOP == role.getType()){
//            bo.setShopId(employee.getShopId());
//        }
//        if(CrmConstant.Role.Type.TEAM == role.getType()){
//            bo.setTeamId(employee.getTeamId());
//        }
        if(employee.getRoleId().intValue() == 0 || CrmConstant.Role.Type.ADMIN == role.getType().byteValue()){
            bo.setOrgId(employee.getOrgId());
        }else if(CrmConstant.Role.Type.SHOP == role.getType().byteValue()){
            bo.setShopId(employee.getShopId());
        }else if(CrmConstant.Role.Type.TEAM == role.getType().byteValue()){
            bo.setTeamId(employee.getTeamId());
        }else{
            bo.setTeamId(employee.getTeamId());
        }
        bo.setPublicPool(CrmConstant.Customer.PublicPool.Y);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("查询公共池分页成功",pageBO);
    }





    /**
     * 公共池/待分配：分配
     * @param
     * @return
     */
    @PostMapping("/customer/distribute")
    @ResponseBody
    public ResultVO distributeCustomer(@RequestBody() CustomerBO bo,@RequestParam(value = "distribute",defaultValue = "0") String distribute){
        if(null == bo)
            return ResultVO.fail("再分配，参数不能为空",bo);
        if(ListUtil.isEmpty(bo.getIdList()))
            return ResultVO.fail("请选择要分配的客户",bo);
//        if(null == bo.getShopId())
//            return ResultVO.fail("请选择门店",bo);
//        if(null == bo.getTeamId())
//            return ResultVO.fail("请选择团队",bo);
//        if(null == bo.getEmployeeId())
//            return ResultVO.fail("请选择员工",bo);
        // 表示是多选客户手动分配个某一个员工
        if(!ListUtil.isEmpty(bo.getIdList())){
            for(Long customerId : bo.getIdList()){
                CustomerPO po = customerService.getCustomerById(customerId);
                if(null == po){
                    continue;
                }
                if(null != bo.getEmployeeId()){
                    po.setPublicPool(CrmConstant.Customer.PublicPool.N);// 再分配后，改客户不属于公共池
                }
                po.setShopId(bo.getShopId());
                po.setTeamId(bo.getTeamId());
                boolean firstDistribute = false;
                // 如果firstEmployee没有值 并且 不是自建客户, 则表示是第一次分配,发送短息提醒
                if((null == po.getFirstEmployee() || po.getFirstEmployee().longValue() <= 0l) && JudgeUtil.in(po.getZijian(),CrmConstant.Customer.Zijian.NO)){
                    firstDistribute = true;
                    po.setFirstEmployee(bo.getEmployeeId());
                    po.setDistributeDate(new Date());
                    po.setProgress(CrmConstant.Customer.Progress.IS_DIS);
                }else{
                    po.setAgainDistribute(CrmConstant.Customer.Again.TRUE);// 标识为再分配客户
                    po.setAgainDistributeDate(new Date());// 记录再分配日期
                    po.setAgainEmployeeId(bo.getEmployeeId());//再分配员工id
                }
                po.setEmployeeId(bo.getEmployeeId());
                customerService.updateCustomer(po);

                // 给员工推送页面消息
                OrgEmployeePO employee = employeeService.getEmployeeById(bo.getEmployeeId());
                if(firstDistribute && null != employee){
                    OrgPO orgPO = orgService.getOrgById(employee.getOrgId());
                    String employeeMsg = String.format(SmsApi.MESSAGE_TEMPLATE_DIS,orgPO.getNickname(),po.getName());
                    new WDSms().sendMessage(employee.getPhone(),employeeMsg,CrmConstant.CreateBy.DIS);
                }

                // 给员工推送页面消息
                if(null != employee){
                    employeeMsgService.addMsg(new EmployeeMsgPO(employee.getId(),employee.getPhone(),String.format("再分配新客户{%s}请及时处理",po.getName()),CrmConstant.Employee.Msg.Status.NEW));
                }
            }
        }else{
            customerService.updateCustomer(bo);
        }
        return ResultVO.success("全部客户客户-分页成功",bo);
    }


    /**
     * 公共池删除功能
     * @param bo
     * @return
     */
    @RequestMapping("/customer/public/del")
    @ResponseBody
    public ResultVO delCustomer(@RequestBody() CustomerBO  bo){
        if(ListUtil.isEmpty(bo.getIdList())){
            ResultVO.fail("请选择要删除的客户",null);
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        if(0L == employee.getRoleId().longValue() || CrmConstant.Role.Type.ADMIN == employee.getRole().getType() ){
            List<Long> data = bo.getIdList();
            for(int index = 0;index<data.size();index++){
                Long id = data.get(index);
                customerService.delCustomer(id);
            }
            return ResultVO.success("删除客户成功",null);
        }
        return ResultVO.fail("您没有权限,请联系管理员!",null);
    }


    /**
     * 领取客户
     * @param bo CustomerBO
     * @return
     */
    @RequestMapping("/customer/public/get")
    @ResponseBody
    public ResultVO getCustomer(@RequestBody() CustomerBO  bo){
        if(ListUtil.isEmpty(bo.getIdList())){
            ResultVO.fail("请选择要领取的客户",null);
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        List<Long> data = bo.getIdList();
        for(int index = 0;index<data.size();index++){
            Long customerId = data.get(index);
            CustomerPO po = customerService.getCustomerById(customerId);
            if(null == po){
                continue;
            }

            po.setShopId(employee.getShopId());
            po.setTeamId(employee.getTeamId());
            po.setEmployeeId(employee.getId());
            po.setPublicPool(CrmConstant.Customer.PublicPool.N);// 再分配后，改客户不属于公共池
            if(po.getProgress().byteValue() <= CrmConstant.Customer.Progress.IS_DIS){
                po.setProgress(CrmConstant.Customer.Progress.IS_DIS);
            }
            // 如果客户没有分配过,则设置第一次分配的员工
            if(null == po.getFirstEmployee() || po.getFirstEmployee().longValue() <= 0l){
                po.setFirstEmployee(employee.getId());
                po.setDistributeDate(new Date());
            }else{
                po.setAgainDistribute(CrmConstant.Customer.Again.TRUE);// 标识为再分配客户
                po.setAgainDistributeDate(new Date());// 记录再分配日期
                po.setAgainEmployeeId(employee.getId());//再分配员工id
            }
            customerService.updateCustomer(po);
        }
        return ResultVO.success("领取[+"+bo.getIdList().size()+"]成功",null);
    }

    /**
     * 导入客户
     * @return
     */
    @RequestMapping("/import/excel/customer")
    @ResponseBody
    public ResultVO importCustomer(@RequestParam("file") MultipartFile multipartFile){
        if (null == multipartFile || multipartFile.isEmpty()) {
            return ResultVO.fail("导入文件失败,请选择导入客户excel文件",null);
        }
        StringBuffer failMobile = new StringBuffer("");
        int successCount = 0;
        try{
            List<String[]> datas = XLSXCovertCSVReader.readerExcel(multipartFile.getInputStream(), "Sheet1", 21);
            if(ListUtil.isEmpty(datas)){
                return ResultVO.success("导出客户成功:没有客户",null);
            }
            OrgEmployeeBO e = LoginUtil.getLoginEmployee();
            for (int index =0;index < datas.size();index++){
                if(index == 0){
                    continue;
                }
                boolean isSuccess = saveImportCustomer(datas.get(index),failMobile,e);
                if(isSuccess)
                    successCount++;
            }
        }catch (Exception e){
            LOG.error("公共池导入客户失败:{}",e.getMessage(),e);
            return ResultVO.fail("读取excel失败",null);
        }
        return ResultVO.success("导入客户公共池成功:"+successCount,failMobile.toString());
    }

    private boolean saveImportCustomer(String[] array,StringBuffer failMobile,OrgEmployeeBO employee){
        if(null == array || array.length <= 0){
            return false;
        }
        String mobile = array[4];
        String mobileMd5 = Md5Util.encryptMd5(mobile);
        CustomerPO md5 = customerService.getCustomerByMd5(employee.getOrgId(), mobileMd5);
        if(null != md5){
            if(null == failMobile || StringUtils.isEmpty(failMobile)){
                failMobile.append(mobile);
            }else{
                failMobile.append(",").append(mobile);
            }
            return false;
        }
        try{
            CustomerPO customer = new CustomerPO();
            customer.setOrgId(employee.getOrgId());
            customer.setChannel(Long.valueOf(array[0]));
            customer.setMedia(StringUtils.isEmpty(array[1])?"自建":array[1]);
            customer.setCity(array[2]);
            customer.setName(array[3]);
            customer.setMobile(mobile);
            customer.setMobileMd5(mobileMd5);
            customer.setSex(NumberUtils.parseNumber(array[5],Byte.class));
            customer.setAge(NumberUtils.parseNumber(array[6],Integer.class));
            customer.setNeed(array[7]);
            customer.setField2(array[8]);
            customer.setField3(array[9]);
            customer.setField4(array[10]);
            customer.setField5(array[11]);
            customer.setField6(array[12]);
            customer.setField7(array[13]);
            customer.setField8(array[14]);
            customer.setField9(array[15]);
            customer.setField10(array[16]);
            customer.setField11(array[17]);
            customer.setField11(array[18]);
            customer.setRemark(array[19]);
            customer.setLabel(array[20]);
            customer.setProgress(CrmConstant.Customer.Progress.INIT);
            customer.setPublicPool(CrmConstant.Customer.PublicPool.Y);
            customer.setZijian(CrmConstant.Customer.Zijian.YES);
            customer.setCreateBy(employee.getName()+"excel");
            customer.setCreateDate(new Date());
            customerService.addCustomer(customer);
            return true;
        }catch (Exception e){
            if(null == failMobile || StringUtils.isEmpty(failMobile)){
                failMobile.append(mobile);
            }else{
                failMobile.append(",").append(mobile);
            }
            LOG.error("导入客户:保存客户异常:{}",e.getMessage(),e);
            return false;
        }

    }


}
