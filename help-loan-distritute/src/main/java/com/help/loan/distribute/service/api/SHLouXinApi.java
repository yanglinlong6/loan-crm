package com.help.loan.distribute.service.api;

import com.ec.v2.config.Config;
import com.ec.v2.entity.config.ConfigCrmGroupTagResp;
import com.ec.v2.entity.config.ConfigResponse;
import com.ec.v2.entity.customer.*;
import com.ec.v2.entity.organization.FindUserInfoVO;
import com.ec.v2.entity.organization.OrganizationResponse;
import com.ec.v2.entity.organization.UserInfoDTO;
import com.ec.v2.service.Customer;
import com.ec.v2.service.Organization;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 上海娄鑫电子
 *
 * 账号:13917038452
 */
@Component("apiSender_20014")
public class SHLouXinApi implements  ApiSender{

    private static final Logger log = LoggerFactory.getLogger(SHLouXinApi.class);

    private static final String appid = "689414313860923392";

    private static final String appSecret = "0nhOOF8c6ojRHiDeiB3";

    private static final long corpid = 14801891;

    private static final long userId = 14801892;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    private static final String sendUr = "https://open.workec.com/v2/customer/addCustomer";
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return  sendResult(po,select);
        }catch (Exception e){
            log.error("[上海娄鑫电子]分发异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海娄鑫电子】分发未知异常："+e.getMessage()));
            return new SendResult(false,"[上海娄鑫电子]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws IOException {

        isHaveAptitude(po);
        Config.initConfig(corpid, appid, appSecret);
        AddCustomerVo dto = new AddCustomerVo();
        dto.setOptUserId(userId);

        List<CustomerDetail> list = new ArrayList<>();
        CustomerDetail customerDetail = new CustomerDetail();
        customerDetail.setName(po.getName());
        customerDetail.setMobile(po.getMobile());
        customerDetail.setGender(po.getGender());
        customerDetail.setFollowUserId(userId);
        customerDetail.setMemo(getMemo(po));
        customerDetail.setChannelId(115);
        list.add(customerDetail);
        dto.setList(list);

        //{"code":200,"data":{"failureList":[],"successIdList":[{"crmId":4017159899,"index":0}]},"msg":"成功"}
        AddCustomerResp res = Customer.addCustomer(dto);
        log.info("【上海娄鑫电子】分发结果：{}", JSONUtil.toJsonString(res));
        if(200 != res.getCode()){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海娄鑫电子】分发失败："+res));
            return new SendResult(false,JSONUtil.toJsonString(res));
        }
        List<?> successIdList = res.getData().getSuccessIdList();
        if(successIdList.size() > 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海娄鑫电子】分发成功："+res));
            return new SendResult(true,JSONUtil.toJsonString(res));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海娄鑫电子】分发重复："+res));
        return new SendResult(false,JSONUtil.toJsonString(res));
    }

    private String getMemo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getPublicFund().contains("有，")){
            memo.append("有公积金").append("，");
        }
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            memo.append("有银行代发工资").append(",");
        }
        if(JudgeUtil.in(po.getHouse(),1,2)){
            memo.append("有商品房").append(",");
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            memo.append("有车").append(",");
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            memo.append("有商业寿险保单").append(",");
        }
        return memo.toString();
    }

    public static void main(String[] args) throws IOException {


//        Config.initConfig(corpid, appid, appSecret);
//
//        FindUserInfoVO findUserInfoVO = new FindUserInfoVO();
//        findUserInfoVO.setAccount("13917038452");
//        OrganizationResponse<UserInfoDTO> response = Organization.findUserInfoById(findUserInfoVO);
//
//        System.out.println(JSONUtil.toJsonString(response));


        // 1. 在同一个进程中，第一次调用，需要初始化配置， 后续调用无需初始化, 直接从第二步开始
        // 初始化配置信息:115
//        Config.initConfig(corpid, appid, appSecret);
//        CustomerResp<List<CrmSourceDTO>> res = Customer.getChannelSource();
//        System.out.println(JSONUtil.toJsonString(res));


        //标签信息
//        Config.initConfig(corpid, appid, appSecret);
//        ConfigResponse<ConfigCrmGroupTagResp> res = com.ec.v2.service.Config.getLabelInfo();
//        System.out.println(JSONUtil.toJsonString(res));


        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
        po.setName("海测试6");
        po.setMobile("13671948207");
        po.setCity("上海市");
        po.setLoanAmount("《3-10万》");
        po.setCar(0);
        po.setHouse(0);
        po.setCompany(0);
        po.setPublicFund("有，个人月缴300-800元");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setOccupation(0);
        po.setCreditCard(0);
        po.setGender(1);
        po.setAge(33);
        po.setUpdateDate(new Date());
        ApiSender api = new SHLouXinApi();
        System.out.println(api.send(po,null));
    }
}
