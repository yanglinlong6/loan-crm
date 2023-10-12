package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.filter.EmojiFilter;
import com.help.loan.distribute.common.utils.HttpUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 上海正佰融信息科技有限公司
 * 上海市浦东新区福山路450号15a
 * 卢木校
 * 13726022584
 */
//@Component("apiSender_10065")
public class ZhengBaiRongApi implements ApiSender {



    private static final Logger log = LoggerFactory.getLogger(ZhengBaiRongApi.class);

//    private static final String sendUrl = "http://test.yxjinfu.com/index.php/api/customer/import_df";

    private static final String sendUrl = "http://qudao.yxjinfu.com/api/qudao/import_df";

    private  static final int file_id = 716;

    private static final String appSecret = "eff7d5dba32b4da32d9a67a519434d3f";





    @Autowired
    private DispatcheRecDao dispatcheRecDao;
    @Override
    public SendResult send(UserAptitudePO po, UserDTO select) {
        try {
            return send2(po,select);
        }catch (Exception e){
            log.error("【正佰融】分发异常：{}",e.getMessage(),e);
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【正佰融】分发异常:"+e.getMessage()));
            return new SendResult(false,"");
        }
    }


    public SendResult send2(UserAptitudePO po, UserDTO select){

        JSONObject data = new JSONObject();
        data.put("appSecret",appSecret);
        data.put("file_id",file_id);
        if(StringUtils.isBlank(po.getName()) && null != select){
            String userInfo = WechatCenterUtil.getUserInfo(po.getUserId(), "", "");
            JSONObject parse = JSON.parseObject(userInfo);
            if(StringUtils.isBlank(po.getName())) {
                if(StringUtils.isBlank(parse.getString("openid"))) {
                    po.setName("公众号用户");
                } else {
                    po.setName(EmojiFilter.filterEmoji(parse.getString("nickname"),po.getUserId()));
                }
            }
        }
        data.put("name",po.getName());
        data.put("mobile",po.getMobile());
        data.put("age",(null == po.getAge() || po.getAge() <=20)?30:po.getAge());
        data.put("sex",(null == po.getGender())?0:po.getGender());
        data.put("city",po.getCity());
        data.put("is_house", JudgeUtil.in(po.getHouse(),1,2)?1:0);
        data.put("is_car",JudgeUtil.in(po.getCar(),1,2)?1:0);
        data.put("is_company",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("is_credit",JudgeUtil.in(po.getCreditCard(),1,2)?1:0);
        data.put("is_insurance",JudgeUtil.in(po.getInsurance(),1,2)?1:0);
        data.put("is_social",0);
        data.put("is_fund",po.getPublicFund().contains("有，")?1:0);
        data.put("is_work",JudgeUtil.in(po.getGetwayIncome(),1,2)?1:0);
        data.put("is_tax",JudgeUtil.in(po.getCompany(),1)?1:0);
        data.put("webank",0);
        data.put("money_demand", LoanAmountUtil.transform(po.getLoanAmount()));

        String response = HttpUtil.postForJSON(sendUrl,data);
        log.info("【正佰融】分发结果：{}",response);
        JSONObject jsonObject = JSONUtil.toJSON(response);
        if(jsonObject.getIntValue("code") == 0){
            dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),1,"【正佰融】分发成功:"+response));
            return new SendResult(true,response);
        }else{
            String msg = jsonObject.getString("msg");
            if(msg.contains("已存在")){
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),0,"【正佰融】分发重复:"+response));
                return new SendResult(false,"【正佰融】重复："+response);
            }else
                dispatcheRecDao.add(getDispatcheRecPO(po.getOrgId(),po.getId(),2,"【正佰融】分发失败:"+response));
                return new SendResult(false,"【正佰融】失败："+response);
        }
    }

//    public static void main(String[] args){
//        UserAptitudePO po = new UserAptitudePO();
//        po.setUserId("29e78c98feee4aeb9f9677d9e2dfa251");
//        po.setName("刘芬");
//        po.setMobile("18301799110");
//        po.setCity("上海市");
//        po.setLoanAmount("100000");
//        po.setCompany(0);
//        po.setPublicFund("没有公积金");
//        po.setCar(0);
//        po.setHouse(0);
//        po.setInsurance(0);
//        po.setGetwayIncome(1);
//        po.setOccupation(0);
//        po.setCreditCard(0);
//        po.setUpdateDate(new Date());
//        ZhengBaiRongApi api = new ZhengBaiRongApi();
//        System.out.println(api.send(po,null));
//    }
}
