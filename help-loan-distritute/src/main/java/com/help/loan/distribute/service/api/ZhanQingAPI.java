package com.help.loan.distribute.service.api;

import com.ec.v2.config.Config;
import com.ec.v2.entity.customer.AddCustomerResp;
import com.ec.v2.entity.customer.AddCustomerVo;
import com.ec.v2.entity.customer.CustomerDetail;
import com.ec.v2.service.Customer;
import com.help.loan.distribute.common.utils.DateUtil;
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
import java.util.*;

/**
 * 上海盏清 10081
 */
@Component("apiSender_10081")
public class ZhanQingAPI implements  ApiSender {


    private static final Logger log = LoggerFactory.getLogger(ZhanQingAPI.class);

    private static final long corpid = 13956994;

    private static final String appid = "626057413123899392";

    private static final String secret = "2ROxggmdMOQlqTZU8OV";

    private static final String account = "13262276641";

    private static final long userId = 13956995l;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return send2(po,select);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海盏清】分发未知异常："+e.getMessage()));
            return new SendResult(false,"【上海盏清】分发未知异常："+e.getMessage());
        }

    }

    private SendResult send2(UserAptitudePO po, UserDTO select) throws IOException {

        Config.initConfig(corpid, appid, secret);
        AddCustomerVo dto = new AddCustomerVo();
        dto.setOptUserId(userId);

        List<CustomerDetail> list = new ArrayList<>();
        CustomerDetail customerDetail = new CustomerDetail();
        customerDetail.setName(po.getName());
        customerDetail.setMobile(po.getMobile());
        customerDetail.setFollowUserId(dto.getOptUserId());
        customerDetail.setMemo(getMemo(po));

        Map<String,String> fields = new HashMap<>();
        fields.put("81391448",po.getLoanAmount());
        fields.put("81391458",po.getPublicFund());
        fields.put("81391454",JudgeUtil.in(po.getGetwayIncome(),1,2)?"银行代发":"未知");
        fields.put("81391461",JudgeUtil.in(po.getHouse(),1,2)?"有房产":"未知");
        fields.put("81391462",JudgeUtil.in(po.getCar(),1,2)?"有车":"未知");
        fields.put("81391463",JudgeUtil.in(po.getInsurance(),1,2)?"有人寿保单":"未知");
        fields.put("81391489",po.getCity());
        fields.put("81391466", DateUtil.formatToString(new Date(),DateUtil.yyyyMMddHHmmss2));
        customerDetail.setFields(fields);

        list.add(customerDetail);
        dto.setList(list);

        //{"code":200,"data":{"failureList":[],"successIdList":[{"crmId":4017159899,"index":0}]},"msg":"成功"}
        AddCustomerResp res = Customer.addCustomer(dto);
        log.info("【上海盏清】分发结果：{}", JSONUtil.toJsonString(res));
        if(200 != res.getCode()){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【上海盏清】分发失败："+res));
            return new SendResult(false,JSONUtil.toJsonString(res));
        }
        List<?> successIdList = res.getData().getSuccessIdList();
        if(successIdList.size() > 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【上海盏清】分发成功："+res));
            return new SendResult(true,JSONUtil.toJsonString(res));
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【上海盏清】分发重复："+res));
        return new SendResult(false,JSONUtil.toJsonString(res));
    }


    private java.lang.String getMemo(UserAptitudePO po){
        StringBuffer memo = new StringBuffer();
        if(po.getPublicFund().contains("有，")){
            memo.append(po.getPublicFund()).append("，");
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
//        Config.initConfig(13956994, "626057413123899392", "2ROxggmdMOQlqTZU8OV");

//        FindUserInfoVO findUserInfoVO = new FindUserInfoVO();
//        findUserInfoVO.setAccount("13262276641");
//        OrganizationResponse<UserInfoDTO> response = Organization.findUserInfoById(findUserInfoVO);
//        System.out.println(JSONUtil.toJsonString(response));

//        AddCustomerVo dto = new AddCustomerVo();
//        dto.setOptUserId(userId);
//
//        List<CustomerDetail> list = new ArrayList<>();
//
//        CustomerDetail cd = new CustomerDetail();
//        cd.setName("测试姓名");
//        cd.setMobile("18879819525");
//        cd.setFollowUserId(dto.getOptUserId());
//        list.add(cd);
//
//        dto.setList(list);
//
//        AddCustomerResp res = Customer.addCustomer(dto);
//
//        System.out.println(JSONObject.toJSONString(res));
//

//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
//        po.setName("海测试3");
//        po.setMobile("13671948206");
//        po.setCity("上海市");
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
//        ZhanQingAPI api = new ZhanQingAPI();
//        System.out.println(api.send(po,null));

//        Config.initConfig(corpid, appid, secret);
//        ConfigResponse<ConfigCustomFieldResp> res = com.ec.v2.service.Config.getFieldMapping();
//        System.out.println(JSONObject.toJSONString(res));
    }
}
