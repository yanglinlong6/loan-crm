package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
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
 * 临沂享贷科技
 */
@Component("apiSender_20109")
public class XiangdaiKejiApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(XiangdaiKejiApi.class);

    private static final String key = "fluxZhudai*_key";

    /**租户id*/
    private static final String tenanId = "1469592341300953089";

    /**平台id*/
    private static final String trafficPlatformId = "1575397893408935938";

    private static final String checkUrl = "https://king.cdsxlc.cn/saasbg/openapi/interface/query/phones";

    private static final String sendUrl  = "https://king.cdsxlc.cn/saasbg/openapi/interface/save/customer";
    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try{
            return sendResult(po,select);
        }catch (Exception e){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[临沂享贷科技]推送未知异常："+e.getMessage()));
            LOG.error("[临沂享贷科技]推送异常:{}-{}",e.getMessage(),e);
            return new SendResult(false,"[临沂享贷科技]推送异常："+e.getMessage());
        }

    }

    private SendResult sendResult(UserAptitudePO po, UserDTO select){



        JSONObject phone = new JSONObject();
        phone.put("phone",MD5Util.getMd5String(po.getMobile()));

        JSONArray customers = new JSONArray();
        customers.add(phone);
        JSONObject data = new JSONObject();
        data.put("customers",customers);
        data.put("tenantId",tenanId);
        JSONArray dataArray = new JSONArray();
        dataArray.add(data);

        String cotent = "data="+dataArray.toJSONString()+"&trafficPlatformId="+trafficPlatformId+key;
        String sign = MD5Util.getMd5String(cotent);

        JSONObject chack = new JSONObject();
        chack.put("data",dataArray.toJSONString());
        chack.put("trafficPlatformId",trafficPlatformId);
        chack.put("sign",sign);
        // {"code":200,"msg":"操作成功","data":[{"customers":[{"phone":"13632965532","status":0}],"tenantId":"1405729496904110081"}],"success":true}
        String result = restTemplate.postForObject(checkUrl,chack,String.class);
        LOG.info("[临沂享贷科技]撞库结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        int code = resultJSON.getIntValue("code");
        JSONObject json = resultJSON.getJSONArray("data").getJSONObject(0);
        JSONObject customerJson = json.getJSONArray("customers").getJSONObject(0);
        if(200 == code
                && 0 == customerJson.getIntValue("status")){
            return register(po,select);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[临沂享贷科技]撞库重复："+result));
        return new SendResult(false,"[临沂享贷科技]撞库重复:"+result);
    }
    private SendResult register(UserAptitudePO po, UserDTO select){
        isHaveAptitude(po);
        JSONObject customer = new JSONObject();
        customer.put("phone",po.getMobile());
        customer.put("name",po.getName());
        customer.put("amount", LoanAmountUtil.transform(po.getLoanAmount()));
        if(JudgeUtil.in(po.getCompany(),1)){
            customer.put("customerType",2);
        }else{
            customer.put("customerType",1);
        }
        customer.put("car", JudgeUtil.in(po.getCar(),1,2)?"有":"无");
        customer.put("house", JudgeUtil.in(po.getHouse(),1,2)?"有":"无");
        customer.put("age",po.getAge());
        customer.put("purpose","消费贷款");
        customer.put("deadline",12);
        customer.put("social",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        customer.put("overdue","无逾期");
        customer.put("salary",JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无");
        customer.put("accumulation",po.getPublicFund().contains("有，")?"有":"无");
        customer.put("insurance",JudgeUtil.in(po.getInsurance(),1,2)?"有":"无");
        customer.put("type",JudgeUtil.in(po.getCompany(),1)?"法人":"");
        customer.put("credit","无");
        customer.put("weili","无");
        customer.put("points","无");
        customer.put("resource",parseAccount(po.getChannel()));
        customer.put("provinceCode",370000); // 辽宁省
        customer.put("cityCode",371300);//大连市
        JSONArray customers = new JSONArray();
        customers.add(customer);

        JSONObject data = new JSONObject();
        data.put("customers",customers);
        data.put("tenantId",tenanId);

        JSONArray dataArray = new JSONArray();
        dataArray.add(data);

        String sign = MD5Util.getMd5String("data="+dataArray.toJSONString()+"&trafficPlatformId="+trafficPlatformId+key);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data",dataArray.toJSONString());
        jsonObject.put("sign",sign);
        jsonObject.put("trafficPlatformId",trafficPlatformId);
        String result = restTemplate.postForObject(sendUrl,jsonObject,String.class);
        LOG.info("[临沂享贷科技]推送结果：{}",result);
        JSONObject resultJSON = JSONUtil.toJSON(result);
        int code = resultJSON.getIntValue("code");
        JSONObject json = resultJSON.getJSONArray("data").getJSONObject(0);
        JSONObject customerJson = json.getJSONArray("customers").getJSONObject(0);
        if(200 == code
                && 0 == customerJson.getIntValue("status")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"[临沂享贷科技]推送成功："+result));
            return new SendResult(true,"[临沂享贷科技]推送成功:"+result);
        }
        if(200 == code
                && 1 == customerJson.getIntValue("status")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"[临沂享贷科技]推送重复："+result));
            return new SendResult(false,"[临沂享贷科技]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"[享贷科技]推送失败："+result));
        return new SendResult(false,"[临沂享贷科技]推送失败:"+result);
    }

    public static void main(String[] args){

        UserAptitudePO po = new UserAptitudePO();
        po.setUserId("fc1c19f47ad64682984d28f9278b298c");
        po.setName("张测试请忽略2");
        po.setMobile("13632965530");
        po.setCity("临沂市");
        po.setLoanAmount("《3-10万》");
        po.setCar(1);
        po.setHouse(1);
        po.setCompany(1);
        po.setPublicFund("有，个人月缴300-800元");
        po.setGetwayIncome(1);
        po.setInsurance(1);
        po.setAge(33);
        po.setChannel("moerlong-gdt-1026");
        po.setOccupation(1);
        po.setCreditCard(1);
        po.setUpdateDate(new Date());

        ApiSender apiSender = new XiangdaiKejiApi();
        apiSender.send(po,null);

    }
}
