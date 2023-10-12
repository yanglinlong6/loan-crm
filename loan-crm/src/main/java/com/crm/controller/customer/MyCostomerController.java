package com.crm.controller.customer;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.customer.ContractService;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.*;
import com.crm.service.dial.DialService;
import com.crm.service.dial.model.CustomerDialPO;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 我的客户控制器接口
 */
@RestController
public class MyCostomerController {

    private static final Logger log = LoggerFactory.getLogger(MyCostomerController.class);

    @Autowired
    CustomerService customerService;

    @Autowired
    DialService dialService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String APP_TOKEN_PREFIX = "dewu_app_";

    /**
     * 一键拨号功能
     * @return ResultVO
     */
    @RequestMapping("/my/customer/dial")
    public ResultVO dial(@RequestParam("customerId") Long customerId, @RequestParam("mobile")String mobile){
        if(null == customerId || customerId <=0){
            return ResultVO.fail("缺少客户id",null);
        }
        if(StringUtils.isBlank(mobile)){
            return ResultVO.fail("缺少客户手机参数",null);
        }
        byte dial = LoginUtil.getLoginEmployee().getOrg().getDial();
        if(Byte.valueOf("0").byteValue() == dial){
            return ResultVO.success("未开启一键拨号功能",null);
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        CustomerDialPO po = new CustomerDialPO();
        po.setOrgId(employee.getOrgId());
        po.setEmployeeId(employee.getId());
        po.setEmployeePhone(employee.getPhone());
        po.setCustomerId(customerId);
        po.setCustomerPhone(mobile);
        dialService.addDial(po);
        return ResultVO.success("正在拨号,请注意查看app拨号...",null);
    }

    @PostMapping("/my/customer/save/file")
    @ResponseBody
    public ResultVO saveFile(@RequestBody() CustomerBO bo){
        if(null == bo){
            return ResultVO.fail("保存客户资料:缺少客户参数",null);
        }
        if(StringUtils.isBlank(bo.getIdcardBack())
                && StringUtils.isBlank(bo.getIdcardFront())
                && StringUtils.isBlank(bo.getCreditFile())
                && StringUtils.isBlank(bo.getAuthorizeFile())
                && StringUtils.isBlank(bo.getOtherFile())){
            return ResultVO.fail("保存客户资料:缺少客户资料",null);
        }
        customerService.updateCustomer(bo);
        return ResultVO.success("保存客户资料成功",null);
    }



    /**
     * 我的客户--新客户（分页列表）
     * @param pageBO PageBO<CustomerBO>
     * @return
     */
    @PostMapping("/my/customer/new/page")
    @ResponseBody
    public ResultVO newCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO)
            return ResultVO.success("",pageBO);
        CustomerBO customerBO = pageBO.getParamObject();
        if(null == customerBO)
            customerBO = new CustomerBO();
        customerBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        customerBO.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        if(null == customerBO.getProgress() || customerBO.getProgress() <= 0)
            customerBO.setProgress(CrmConstant.Customer.Progress.IS_DIS);
        pageBO.setParamObject(customerBO);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("我的新客户-分页成功",pageBO);
    }

    /**
     * 我的再分配客户（分页列表）
     * @param pageBO
     * @return
     */
    @PostMapping("/my/customer/again/page")
    @ResponseBody
    public ResultVO againCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO)
            return ResultVO.success("",pageBO);
        CustomerBO customerBO = pageBO.getParamObject();
        if(null == customerBO)
            customerBO = new CustomerBO();
        customerBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        customerBO.setAgainDistribute(CrmConstant.Customer.Again.TRUE);
        customerBO.setAgainEmployeeId(LoginUtil.getLoginEmployee().getId());
        pageBO.setParamObject(customerBO);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("再分配客户-分页成功",pageBO);
    }

    /**
     * 我的客户： 协助客户（分页列表）
     * @param pageBO
     * @return
     */
    @PostMapping("/my/customer/help/page")
    @ResponseBody
    public ResultVO helpCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO)
            return ResultVO.success("",pageBO);
        CustomerBO customerBO = pageBO.getParamObject();
        if(null == customerBO)
            customerBO = new CustomerBO();
        customerBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        customerBO.setHelpEmployeeId(LoginUtil.getLoginEmployee().getId());
        pageBO.setParamObject(customerBO);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("协助客户-分页成功",pageBO);
    }



    /**
     * 我的客户-协助
     * @param customerBO
     * @return
     */
    @PostMapping("/my/customer/help")
    @ResponseBody
    public ResultVO helpCustomer(@RequestBody() CustomerBO customerBO){
        if(null == customerBO || ListUtil.isEmpty(customerBO.getIdList()))
            return ResultVO.fail("协助客户是空的",customerBO);
        for(Long id:customerBO.getIdList()){
            CustomerPO po = new CustomerPO();
            po.setId(id);
            po.setHelpEmployeeId(customerBO.getHelpEmployeeId());
            po.setUpdateBy(LoginUtil.getLoginEmployee().getName());
            customerService.updateCustomer(po);
        }
        return ResultVO.success("设置【"+customerBO.getIdList()+"】个客户协助成功",null);
    }

    /**
     * 取消协助客户
     * @param customerBO
     * @return
     */
    @PostMapping("/my/customer/help/cancel")
    @ResponseBody
    public ResultVO helpCancelCustomer(@RequestBody() CustomerBO customerBO){
        if(null == customerBO || ListUtil.isEmpty(customerBO.getIdList()) || null == customerBO.getHelpEmployeeId())
            return ResultVO.fail("协助客户是空的",customerBO);
        for(Long id:customerBO.getIdList()){
            CustomerPO po = new CustomerPO();
            po.setId(id);
            po.setHelpEmployeeId(0l);
            po.setUpdateBy(LoginUtil.getLoginEmployee().getName());
            customerService.updateCustomer(po);
        }
        return ResultVO.success("["+customerBO.getIdList()+"]客户取消协助成功",null);
    }

    /**
     * 我的所有客户
     * @param bo
     * @return
     */
    @PostMapping("/my/customer/all")
    @ResponseBody
    public ResultVO myCustomerAll(@RequestBody() CustomerBO bo){

        List<CustomerBO> list = customerService.getMyAllCustomer(bo);
        return ResultVO.success("查询客户成功",list);
    }


    /**
     * 我的全部客户（分页）
     * @param pageBO
     * @return
     */
    @PostMapping("/my/customer/all/page")
    @ResponseBody
    public ResultVO allCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO)
            return ResultVO.success("",pageBO);
        CustomerBO customerBO = pageBO.getParamObject();
        if(null == customerBO)
            customerBO = new CustomerBO();
        customerBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        customerBO.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        customerBO.setAgainEmployeeId(LoginUtil.getLoginEmployee().getId());
        customerBO.setAll(Byte.valueOf("1"));//标识查询我的所有客户
        customerBO.setPublicPool(CrmConstant.Customer.PublicPool.N);
        pageBO.setParamObject(customerBO);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("协助客户-分页成功",pageBO);
    }

    /**
     * 添加客户信息 CustomerBO
     * @param bo CustomerBO
     * @return CustomerBO
     * zhangqiuping 509124739@qq.com
     */
    @PostMapping("/my/customer/add")
    @ResponseBody
    public ResultVO addCustomer(@RequestBody()CustomerBO bo){
        if(null == bo){
            return ResultVO.fail("更新客户信息失败：缺少客户字段",null);
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        bo.setOrgId(employee.getOrgId());
        bo.setShopId(employee.getShopId());
        bo.setTeamId(employee.getTeamId());
        bo.setCurrentEmployee(employee.getId());
        bo.setEmployeeId(employee.getId());// 设置所属员工id
        bo.setFirstEmployee(employee.getId()); // 手动新添加的客户,第一员工设置为当前员工id
        if(bo.getProgress() != null && bo.getProgress().intValue() <= 0){
            bo.setProgress(CrmConstant.Customer.Progress.ING);
        }
        bo.setZijian(CrmConstant.Customer.Zijian.YES);
        bo.setDistributeDate(new Date());
        customerService.addCustomer(bo);
        return ResultVO.success("添加客成功",null);
    }



    /**
     * 更新客户信息 CustomerBO
     * @param bo CustomerBO
     * @return CustomerBO
     * zhangqiuping 509124739@qq.com
     */
    @PostMapping("/my/customer/update")
    @ResponseBody
    public ResultVO updateCustomer(@RequestBody()CustomerBO bo){
        if(null == bo || null == bo.getId()){
            return ResultVO.fail("跟进客户信息失败：缺少客户信息",null);
        }
        customerService.updateCustomer(bo);
        return ResultVO.success("跟进客户信息成功",LoginUtil.getLoginEmployee().getName());
    }


    @PostMapping("/my/customer/add/pool")
    @ResponseBody
    public ResultVO addPublicPool(@RequestBody()CustomerBO bo){
        customerService.addPublicPool(bo);
        return ResultVO.success("加入公共池成功",null);
    }

    /**
     * 签约合同service
     */
    @Autowired
    ContractService contractService;
    /**
     * 我的客户：签约列表
     * @param page PageBO<CustomerContractBO>
     * @return ResultVO
     */
    @PostMapping("/my/customer/contract")
    @ResponseBody
    public ResultVO getContractPage(@RequestBody(required = false)PageBO<CustomerContractBO> page){
        if(null == page){
            return ResultVO.fail("我的客户-我的签约-无分页查询",null);
        }
        CustomerContractBO bo = page.getParamObject();
        if(null == bo)
            bo = new CustomerContractBO();
        bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        bo.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        contractService.getPage(page);
        return ResultVO.success("我的客户-我的签约-分页查询成功",page);
    }


    @PostMapping("/my/contract/all")
    @ResponseBody
    public ResultVO getContractAll(@RequestBody(required = false)CustomerContractBO bo){
        if(null == bo)
            bo = new CustomerContractBO();
        if(StringUtils.isBlank(bo.getCustomerName()) && StringUtils.isBlank(bo.getCustomerMobile()))
            return ResultVO.fail("",null);
        List<CustomerContractBO> list = contractService.getEmployeeContract(bo);
        return ResultVO.success("我的全部签约",list);
    }

    /**
     * 添加签约合同
     * @param po CustomerContractPO
     * @return
     */
    @PostMapping("/my/customer/contract/add")
    @ResponseBody
    public ResultVO addContract(@RequestBody(required = false) CustomerContractPO po){
        contractService.addCustomerContract(po);
        return ResultVO.success("客户签约成功",null);
    }

    /**
     * 电子签约
     * @param po CustomerContractPO
     * @return
     */
    @PostMapping("/my/customer/contract/esign")
    @ResponseBody
    public ResultVO esignContract(@RequestBody() CustomerContractPO po){
        if(null == po){
            return ResultVO.fail("电子签约:缺少参数",null);
        }
        if(ListUtil.isEmpty(po.getLocations())){
            return ResultVO.fail("电子签约:缺少客户落章参数",null);
        }else{
            if(StringUtils.isBlank(po.getImages()) || !po.getImages().endsWith("pdf")){
                return ResultVO.fail("电子签约:缺少pdf合同文件",null);
            }
            List<LocationPO> locations = po.getLocations();
            for(LocationPO location : locations){
                if(null == location || !JudgeUtil.isNumber(location.getPage()) || !JudgeUtil.isNumber(location.getX()) || !JudgeUtil.isNumber(location.getY())){
                    return ResultVO.fail("电子签约:客户落章参数必须是数字",null);
                }
            }
        }
        po.setEsign(true);
        contractService.addCustomerContract(po);
        return ResultVO.success("客户电子签约成功",null);
    }

    /**
     *
     *
     */
    /**
     *  标记为第三方产品客户
     * @param id 客户id
     * @param thirdPartyRemark 标记为客户为第三方
     * @return ResultVO
     */
    @GetMapping("/my/customer/label/third/party")
    @ResponseBody
    public ResultVO labelThirdParty(@RequestParam("id")Long id,@RequestParam("remark")String thirdPartyRemark){
        if(null == id || id <= 0 || StringUtils.isBlank(thirdPartyRemark)){
            return ResultVO.fail("标记为第三方产品失败:客户id和备注信息[必填]",null);
        }
        CustomerPO customerPO = new CustomerPO(id,CrmConstant.Customer.ThirdParty.Y);
        customerPO.setThirdPartyRemark(thirdPartyRemark);
        customerService.updateCustomer(customerPO);
        return ResultVO.success("标记为第三方产品成功",null);
    }

    /**
     * 修改签约合同信息
     * @param po CustomerContractPO
     * @return
     */
    @PostMapping("/my/customer/contract/update")
    @ResponseBody
    public ResultVO updateContract(@RequestBody(required = false) CustomerContractPO po){
        contractService.updateCustomerContract(po);
        return ResultVO.success("我的签约-修改签约成功",null);
    }

    /**
     * 作废合约
     * @param po CustomerContractPO
     * @return ResultVO
     */
    @PostMapping("/my/customer/contract/abolish")
    @ResponseBody
    public ResultVO abolishContract(@RequestBody(required = false) CustomerContractPO po){
        if(null == po || null == po.getId())
            return ResultVO.success("我的签约-作废-缺少签约id",null);
        po.setState(CrmConstant.Contract.State.INCOMING);
        contractService.updateCustomerContract(po);
        return ResultVO.success("我的签约-作废成功",null);
    }

}
