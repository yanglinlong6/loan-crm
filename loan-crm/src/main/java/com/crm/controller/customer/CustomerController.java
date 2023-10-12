package com.crm.controller.customer;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.ContractService;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.CustomerBO;
import com.crm.service.customer.model.CustomerCommentPO;
import com.crm.service.customer.model.CustomerContractBO;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.dial.VoiceService;
import com.crm.service.dial.model.CustomerVoicePO;
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
import com.crm.service.thirdparty.model.OrgThirdPartyPO;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 客户管理接口
 */
@RestController
public class CustomerController {

//    static final Logger LOG = LoggerFactory.getLogger(CustomerController.class);


    @Autowired
    CustomerService customerService;

    @Autowired
    EmployeeMsgService employeeMsgService;

    @Autowired
    EmployeeService employeeService;

    /**客户通话录音service*/
    @Autowired
    VoiceService voiceService;


    /**
     * 客户通话录音接口
     * @param mobile 客户电话
     * @return ResultVO  data是客户通话录音列表
     */
    @PostMapping("/customer/voice")
    @ResponseBody
    public ResultVO customerVoice(@RequestParam("mobile") String mobile){
        List<CustomerVoicePO> list = voiceService.getCustomerVoice(LoginUtil.getLoginEmployee().getOrgId(),mobile);
        return ResultVO.success("获取客户通话录音成功",list);
    }

    



    /**
     * 客户管理待分配客户（分页列表）
     * @param pageBO PageBO<CustomerBO>
     * @return
     */
    @PostMapping("/customer/wait/page")
    @ResponseBody
    public ResultVO againCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO)
            return ResultVO.success("",pageBO);
        CustomerBO customerBO = pageBO.getParamObject();
        if(null == customerBO)
            customerBO = new CustomerBO();
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        RolePO role = employee.getRole();
        if(!JudgeUtil.in(role.getType(),CrmConstant.Role.Type.ADMIN,CrmConstant.Role.Type.SHOP,CrmConstant.Role.Type.TEAM)){
            return ResultVO.success("您没有权限查看全部客户",pageBO);
        }
        if(CrmConstant.Role.Type.SHOP == role.getType()){
            customerBO.setShopId(employee.getShopId());
        }
        if(CrmConstant.Role.Type.TEAM == role.getType()){
            customerBO.setTeamId(employee.getTeamId());
        }
        customerBO.setProgress(CrmConstant.Customer.Progress.INIT);
        customerBO.setZijian(CrmConstant.Customer.Zijian.NO);
        pageBO.setParamObject(customerBO);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("待分配客户-分页成功",pageBO);
    }


    /**
     * 客户管理：全部客户
     * @param pageBO
     * @return
     */
    @PostMapping("/customer/all/page")
    @ResponseBody
    public ResultVO allCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO)
            return ResultVO.success("",pageBO);
        CustomerBO bo = pageBO.getParamObject();
        if(null == bo)
            bo = new CustomerBO();
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        RolePO role = employee.getRole();
        if(!JudgeUtil.in(role.getType(),CrmConstant.Role.Type.ADMIN,CrmConstant.Role.Type.SHOP,CrmConstant.Role.Type.TEAM)){
            return ResultVO.success("您没有权限查看全部客户",pageBO);
        }
        if(CrmConstant.Role.Type.SHOP == role.getType()){
            bo.setShopId(employee.getShopId());
        }
        if(CrmConstant.Role.Type.TEAM == role.getType()){
            bo.setShopId(employee.getShopId());
            bo.setTeamId(employee.getTeamId());
        }
        if(StringUtils.isBlank(bo.getStartDate())){
            bo.setStartDate("2020-01-01");
        }
        bo.setPublicPool(CrmConstant.Customer.PublicPool.N);
        bo.setAlls(1);
        pageBO.setParamObject(bo);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("全部客户客户-分页成功",pageBO);
    }


    /**
     * 主管评论
     * @param po CustomerCommentPO
     * @return ResultVO
     */
    @PostMapping("/customer/comment")
    @ResponseBody
    public ResultVO comment(@RequestBody() CustomerCommentPO po){
        customerService.addCustomerComment(po);
        // 主管点评了客户, 将评语推送给正在跟进的员工
        CustomerPO customer = customerService.getCustomerById(po.getCustomerId());
        if(null != customer){
            OrgEmployeePO employee = employeeService.getEmployeeById(customerService.getEmployeeId(customer));
            if(null != employee){
                employeeMsgService.addMsg(new EmployeeMsgPO(employee.getId(),employee.getPhone(),String.format("主管点评了[%s]请及时处理",customer.getName()),CrmConstant.Employee.Msg.Status.NEW));
            }
        }
        return ResultVO.success("点评成功",null);
    }







    @RequestMapping("/customer/third/party/page")
    @ResponseBody
    public ResultVO thirdPartyPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO){
            return ResultVO.fail("第三方产品客户分页列表:缺少参数",pageBO);
        }
        CustomerBO bo = pageBO.getParamObject();
        if(null == bo){
            bo = new CustomerBO();
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        RolePO role = employee.getRole();
        if(!JudgeUtil.in(role.getType(),CrmConstant.Role.Type.ADMIN,CrmConstant.Role.Type.SHOP,CrmConstant.Role.Type.TEAM)){
            return ResultVO.success("公共池分页成功",pageBO);
        }
        if(CrmConstant.Role.Type.SHOP == role.getType()){
            bo.setShopId(employee.getShopId());
        }
        if(CrmConstant.Role.Type.TEAM == role.getType()){
            bo.setTeamId(employee.getTeamId());
        }
        bo.setPublicPool(null);
        bo.setThirdparty(CrmConstant.Customer.ThirdParty.Y);
        customerService.getCustomerPage(pageBO);
        pageBO.setParamObject(null);
        return ResultVO.success("第三方产品客户分页列表成功",pageBO);
    }


    @Transactional
    @RequestMapping("/customer/third/party/dis")
    @ResponseBody
    public ResultVO distributeThirdPartyCustomer(@RequestBody() OrgThirdPartyPO po){
        if(null == po ){
            return ResultVO.success("将客户分配给第三方合作公司成功",null);
        }
        List<Long> customerIdList = po.getCustomerIdList();
        if(ListUtil.isEmpty(customerIdList)){
            ResultVO.fail("将客户分配给第三方合作公司:请选择客户",null);
        }
        if(StringUtils.isBlank(po.getCity())){
            ResultVO.fail("将客户分配给第三方合作公司:请选择省份城市",null);
        }
        if(null == po.getProductId() || po.getProductId() <= 0){
            ResultVO.fail("将客户分配给第三方合作公司:请选择省份产品",null);
        }
        if(null == po.getOrgId() || po.getOrgId() <= 0){
            ResultVO.fail("将客户分配给第三方合作公司:请选择第三方公司",null);
        }

        for(Long customerId : customerIdList){
            CustomerPO oldCustomer = customerService.getCustomerById(customerId);
            if(oldCustomer == null){
                ResultVO.fail("将客户分配给第三方合作公司:客户不存在",null);
            }
            CustomerPO newCustomer = JSONUtil.toJavaBean(oldCustomer.toString(),CustomerPO.class);
            Date date = new Date();
            newCustomer.setId(null);
            newCustomer.setOrgId(po.getOrgId());
            newCustomer.setShopId(null);
            newCustomer.setTeamId(null);
            newCustomer.setMedia(LoginUtil.getLoginEmployee().getOrg().getNickname());
            newCustomer.setThirdparty(CrmConstant.INIT);
            newCustomer.setNeed(oldCustomer.getNeed());
            newCustomer.setProgress(CrmConstant.Customer.Progress.INIT);
            newCustomer.setFit(CrmConstant.Customer.Fit.FIT);
            newCustomer.setCall(CrmConstant.Customer.Call.CALL);
            newCustomer.setCreateDate(date);
            newCustomer.setUpdateDate(date);
            newCustomer.setCreateBy(LoginUtil.getLoginEmployee().getName());
            newCustomer.setOldId(customerId);
            newCustomer.setOldOrgId(oldCustomer.getOrgId());
            customerService.addCustomer(newCustomer);

            //更新客户已经分配给第三方机构了
            oldCustomer.setThirdpartyOrgId(po.getOrgId());
            customerService.updateCustomer(oldCustomer);
        }
        return ResultVO.success("将客户分配给第三方合作公司成功",null);
    }

}
