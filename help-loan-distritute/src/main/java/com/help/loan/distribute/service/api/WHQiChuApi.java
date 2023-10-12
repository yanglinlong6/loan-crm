package com.help.loan.distribute.service.api;

import com.ec.v2.config.Config;
import com.ec.v2.entity.customer.AddCustomerResp;
import com.ec.v2.entity.customer.AddCustomerVo;
import com.ec.v2.entity.customer.CustomerDetail;
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
 * 武汉启楚
 */
@Component("apiSender_10114")
public class WHQiChuApi implements  ApiSender{

    private static final Logger log = LoggerFactory.getLogger(WHQiChuApi.class);

    private static final String appid = "678639157425209344";

    private static final String appSecret = "QqLM5W44EB4nQAGyQbL";

    private static final long corpid = 14851504;

    private static final long userId = 14851505;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    private static final String sendUr = "https://open.workec.com/v2/customer/addCustomer";
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return  sendResult(po,select);
        }catch (Exception e){
            log.error("[武汉启楚]分发异常:{}",e.getMessage(),e);
            return new SendResult(false,"[武汉启楚]分发异常:"+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select) throws IOException {

        isHaveAptitude(po);
        Config.initConfig(corpid, appid, appSecret);
        AddCustomerVo dto = new AddCustomerVo();
        dto.setOptUserId(userId);

        List<CustomerDetail> list = new ArrayList<>();
        CustomerDetail customerDetail = new CustomerDetail();
        customerDetail.setName(po.getName()+"_DC");
        customerDetail.setMobile(po.getMobile());
        customerDetail.setGender(po.getGender());
        customerDetail.setFollowUserId(userId);
//        customerDetail.setChannelId(44257);
        customerDetail.setMemo(getMemo(po));
        list.add(customerDetail);
        dto.setList(list);

        //{"code":200,"data":{"failureList":[],"successIdList":[{"crmId":4017159899,"index":0}]},"msg":"成功"}
        AddCustomerResp res = Customer.addCustomer(dto);
        log.info("【武汉启楚】分发结果：{}", JSONUtil.toJsonString(res));
        if(200 != res.getCode()){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【武汉启楚】分发失败："+res));
            return new SendResult(false,JSONUtil.toJsonString(res));
        }
        List<?> successIdList = res.getData().getSuccessIdList();
        if(successIdList.size() > 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【武汉启楚】分发成功："+res));
            return new SendResult(true,JSONUtil.toJsonString(res));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【武汉启楚】分发重复："+res));
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

//    public static void main(String[] args) throws IOException {
//
//
////        Config.initConfig(corpid, appid, appSecret);
////
////        FindUserInfoVO findUserInfoVO = new FindUserInfoVO();
////        findUserInfoVO.setAccount("17602180185");
////        OrganizationResponse<UserInfoDTO> response = Organization.findUserInfoById(findUserInfoVO);
////
////        System.out.println(JSONUtil.toJsonString(response));
//
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海测试5");
//        po.setMobile("13671948205");
//        po.setCity("武汉市");
//        po.setLoanAmount("《3-10万》");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setCompany(0);
//        po.setPublicFund("有，个人月缴300-800元");
//        po.setGetwayIncome(1);
//        po.setInsurance(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        ApiSender api = new WHQiChuApi();
//        System.out.println(api.send(po,null));
//    }
}
