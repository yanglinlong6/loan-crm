package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 北京招信金联信息科技有限公司 20097
 */
@Component("apiSender_20097")
public class BJZhaoxinjinlianApi implements ApiSender{

    private static final Logger LOG = LoggerFactory.getLogger(BJZhaoxinjinlianApi.class);

    private static final String checkUrl = "http://1.13.13.245:8080/api/customer/md5check?md5=%s";

    private static final String sendUrl = "http://1.13.13.245:8080/api/customer/add?name=%s&sex=%s&mobile=%s&city=%s&age=%s&shebao=%s&gongjijin=%s&license=%s&house=%s&car=%s&isbankpay=%s&baodan_is=%s&source=%s&meiti=%s&remark=%s";

    @Autowired
    DispatcheRecDao dispatcheRecDao;

    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return sendResult(po,select);
        }catch (Exception e){
            LOG.error("[北京招信金联]推送异常:{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京招信金联】推送异常："+e.getMessage()));
            return new SendResult(false,"[北京招信金联]推送异常:"+e.getMessage());
        }

    }

    private static int index = 0;

    private SendResult sendResult(UserAptitudePO po, UserDTO select){
        String url = String.format(checkUrl, MD5Util.getMd5String(po.getMobile()));
        String result = HttpUtil.postFormForObject(url,new JSONObject());
        if(12 != JSONUtil.toJSON(result).getInteger("code")){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【北京招信金联】推送重复："+result));
            return new SendResult(false,"[北京招信金联]推送重复:"+result);
        }
        String sex = JudgeUtil.in(po.getGender(),1)?"男":"女";
        String city = po.getCity().endsWith("市")?po.getCity().substring(0,po.getCity().length()-1):po.getCity();
        String shebao = JudgeUtil.contain(po.getPublicFund(),"有，")?"有":"无";
        String gongjijin = JudgeUtil.contain(po.getPublicFund(),"有，")?"有":"无";
        String baodan = JudgeUtil.in(po.getInsurance(),1,2)?"有":"无";
        String license = JudgeUtil.in(po.getCompany(),1)?"有":"无";
        String house = JudgeUtil.in(po.getHouse(),1,2)?"有":"无";
        String car = JudgeUtil.in(po.getCar(),1,2)?"有":"无";
        String isbankpay = JudgeUtil.in(po.getGetwayIncome(),1,2)?"有":"无";
//        String source = (index%2)==0?"zxf":"zs-zxf";
        url = String.format(sendUrl,po.getName(),sex,po.getMobile(),city,po.getAge(),shebao,gongjijin,license,house,car,isbankpay,baodan,"zxf",parseAccount(po.getChannel()),getContent(po));
        result = HttpUtil.postFormForObject(url,new JSONObject());
        LOG.info("[北京招信金联]推送结果:{}",result);
        if(0 == JSONUtil.toJSON(result).getIntValue("code")){
            index++;
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【北京招信金联】推送成功："+result));
            return new SendResult(true,"[北京招信金联]推送重复:"+result);
        }
        dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【北京招信金联】推送失败："+result));
        return new SendResult(false,"[北京招信金联]推送失败:"+result);
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId(null);
//        po.setName("张先森测试");
//        po.setMobile("13915316133");
//        po.setCity("北京市");
//        po.setLoanAmount("8万以上");
//        po.setCompany(1);
//        po.setPublicFund("信用卡,网贷");
//        po.setCar(1);
//        po.setHouse(1);
//        po.setInsurance(1);
//        po.setGetwayIncome(1);
//        po.setOccupation(1);
//        po.setCreditCard(1);
//        po.setAge(30);
//        po.setGender(1);
//        po.setChannel("msg-111");
//        po.setOverdue("即将逾期");
//        po.setUpdateDate(new Date());
//        ApiSender api = new BJZhaoxinjinlianApi();
//        System.out.println(api.send(po,null));
//
//    }
}
