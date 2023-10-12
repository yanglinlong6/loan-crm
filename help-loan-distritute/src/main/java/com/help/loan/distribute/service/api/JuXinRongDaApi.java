package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.ec.v2.config.Config;
import com.ec.v2.entity.config.ConfigCustomFieldResp;
import com.ec.v2.entity.config.ConfigPubicPondResp;
import com.ec.v2.entity.config.ConfigResponse;
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
 * 天津聚鑫融达
 * 天津聚鑫融达商务信息咨询有限公司
 * 电话15102294676，2071715365@qq.com
 */
@Component("apiSender_10110")
public class JuXinRongDaApi implements ApiSender{


    private static final Logger log = LoggerFactory.getLogger(YZHuaXinAPI.class);

    private static final long corpid = 10027543;

    private static final String appid = "679001782713581568";

    private static final String secret = "BqeXGHNEVVwXYQ2Fqny";

    private static final String account = "13752358002";

    private static final long userId = 10027544L;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return send2(po,select);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【津聚鑫融达】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【津聚鑫融达】分发未知异常："+e.getMessage());
        }

    }

    public boolean isHaveAptitude(UserAptitudePO po){

        if(JudgeUtil.in(po.getCar(),1,2))
            return true;
        if(JudgeUtil.in(po.getHouse(),1,2))
            return true;
        if(JudgeUtil.in(po.getCompany(),1))
            return true;
        int index = random.nextInt(3);
        switch (index){
            case 0:
                po.setCar(1);
                break;
            case 1:
                po.setHouse(1);
                break;
            case 2:
                po.setCompany(1);
                break;
            default:po.setCar(1);
        }
        return false;
    }


    private SendResult send2(UserAptitudePO po, UserDTO select) throws IOException {

        isHaveAptitude(po);
        Config.initConfig(corpid, appid, secret);
        AddCustomerVo dto = new AddCustomerVo();
        dto.setOptUserId(userId);

        List<CustomerDetail> list = new ArrayList<>();
        CustomerDetail customerDetail = new CustomerDetail();
        customerDetail.setName(po.getName());
        customerDetail.setMobile(po.getMobile());
        customerDetail.setGender(po.getGender());
        customerDetail.setFollowUserId(userId);
        customerDetail.setChannelId(44257);
        customerDetail.setMemo(getMemo(po));
        list.add(customerDetail);
        dto.setList(list);

        //{"code":200,"data":{"failureList":[],"successIdList":[{"crmId":4017159899,"index":0}]},"msg":"成功"}
        AddCustomerResp res = Customer.addCustomer(dto);
        log.info("【聚鑫融达】分发结果：{}", JSONUtil.toJsonString(res));
        if(200 != res.getCode()){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【津聚鑫融达】分发失败："+res));
            return new SendResult(false,JSONUtil.toJsonString(res));
        }
        List<?> successIdList = res.getData().getSuccessIdList();
        if(successIdList.size() > 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【津聚鑫融达】分发成功："+res));
            return new SendResult(true,JSONUtil.toJsonString(res));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【津聚鑫融达】分发重复："+res));
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
////        Config.initConfig(corpid, appid, secret);
//
////        FindUserInfoVO findUserInfoVO = new FindUserInfoVO();
////        findUserInfoVO.setAccount("13752358002");
////        OrganizationResponse<UserInfoDTO> response = Organization.findUserInfoById(findUserInfoVO);
////        System.out.println(JSONUtil.toJsonString(response));
////
////        AddCustomerVo dto = new AddCustomerVo();
////        dto.setOptUserId(userId);
////
////        ConfigResponse<ConfigCustomFieldResp> res = com.ec.v2.service.Config.getFieldMapping();
////        System.out.println(JSONObject.toJSONString(res));
////
////        List<CustomerDetail> list = new ArrayList<>();
////
////        CustomerDetail cd = new CustomerDetail();
////        cd.setName("测试姓名");
////        cd.setMobile("17751364999");
////        cd.setFollowUserId(dto.getOptUserId());
////        list.add(cd);
////
////        dto.setList(list);
////
////        AddCustomerResp addCustomerResp = Customer.addCustomer(dto);
////
////        System.out.println(JSONObject.toJSONString(addCustomerResp));
//
////        Config.initConfig(corpid, appid, secret);
////        ConfigResponse<ConfigPubicPondResp> res = com.ec.v2.service.Config.getPubicPond();
//        // 4。 处理结果
////        System.out.println(JSONObject.toJSONString(addCustomerResp));
//
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海测试4");
//        po.setMobile("13671948209");
//        po.setCity("北京市");
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
//        ApiSender api = new JuXinRongDaApi();
//        System.out.println(api.send(po,null));
//
//
//    }
}
