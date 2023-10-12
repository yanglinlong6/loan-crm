package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.model.CustomerPO;
import com.help.loan.distribute.service.api.utils.DESUtil;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 诚易融 17844620375  1469292409@qq.com
 */
@Component("apiSender_20130")
public class CSChengyirongApi implements ApiSender {

    private static final Logger LOG = LoggerFactory.getLogger(CSChengyirongApi.class);

    private static final String URL = "https://crm.sykj888.cn/crm/import/customer";

    private static String KEY = "2d59329ea6e845ed91ae1e5f63fa78c6";

    private static Long CHENNEL = 155l;

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Autowired
    CacheService cacheService;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[诚易融]推送异常：{}",e.getMessage(),e);
            if(!JudgeUtil.startWith(po.getChannel(),"360")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[诚易融]推送异常:"+e.getMessage()));
            }
            return new SendResult(false,"[诚易融]推送异常："+e.getMessage());
        }
    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        CustomerPO customerPO = new CustomerPO();
        customerPO.setName(po.getName()+(JudgeUtil.in(po.getType(), DistributeConstant.LoanType.HOUSE)?Heng+"房抵":""));
        customerPO.setMobile(po.getMobile());
        customerPO.setChannel(CHENNEL);
        customerPO.setMedia(getMedia(po.getChannel()));
        customerPO.setCity(po.getCity());
        customerPO.setAge(po.getAge());
        customerPO.setSex(po.getGender().byteValue());
        customerPO.setNeed(LoanAmountUtil.transform(po.getLoanAmount()).toString());
        customerPO.setField2(JudgeUtil.contain(po.getPublicFund(),"有，")?"有":"无");
        customerPO.setField3(JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        customerPO.setField4(JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        customerPO.setField5(JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        customerPO.setField6(JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        customerPO.setField9(JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        customerPO.setField7(JudgeUtil.in(po.getCompany(),1)?"有":"无");
        customerPO.setRemark(getContent5(po,customerPO.getMedia(),cacheService));
        JSONObject data = new JSONObject();
        data.put("channelId",CHENNEL);
        data.put("data",DESUtil.encrypt(KEY,customerPO.toString()));
        System.out.println(data.toJSONString());
        String result = HttpUtil.postForObject(URL,data);
        JSONObject json = JSONUtil.toJSON(result);
        LOG.info("[诚易融]推送结果：{}",result);
        int code = json.getIntValue("code");
        if(200 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[诚易融]推送成功："+result));
            return new SendResult(true,"[诚易融]推送成功："+result);
        }else if(601 == code){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[诚易融]推送重复："+result));
            return new SendResult(false,"[诚易融]推送重复："+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[诚易融]推送失败："+result));
        return new SendResult(false,"[诚易融]推送失败："+result);
    }

    private String getContent5(UserAptitudePO po,String media,CacheService cacheService){
        StringBuffer content = new StringBuffer();
        content.append("申请产品：").append(parseAccount(po.getChannel())).append(";");
        content.append("贷款金额：").append(LoanAmountUtil.transform(po.getLoanAmount())).append(";");
        if(po.getPublicFund().contains("有，"))
            content.append("有300-800公积金").append(";");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            content.append("有银行代发工资").append(";");
        if(JudgeUtil.in(po.getCar(),1,2))
            content.append("有车").append(";");
        if(JudgeUtil.in(po.getInsurance(),1,2));
        content.append("有寿险保单").append(";");
        if(1 == po.getCompany())
            content.append("有企业纳税").append(";");
        if(JudgeUtil.in(po.getHouse()))
            content.append("有本地商品房").append(";");
        if(JudgeUtil.in(po.getCreditCard(),1)){
            content.append("有信用卡").append(";");
        }
        return content.toString();
    }


//    public static void main(String[] args) {
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张测试请忽略");
//        po.setMobile("13410567155");
//        po.setCity("长沙市");
//        po.setAge(32);
//        po.setGender(2);
//        po.setLoanAmount("30000");
//        po.setCompany(1);
//        po.setPublicFund("无");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setChannel("zxf-ttt-1009-108=28");
//        po.setType(DistributeConstant.LoanType.CREDIT);
//        po.setUpdateDate(new Date());
//        ApiSender api = new CSChengyirongApi();
//        System.out.println(api.send(po, null));
//    }
}
