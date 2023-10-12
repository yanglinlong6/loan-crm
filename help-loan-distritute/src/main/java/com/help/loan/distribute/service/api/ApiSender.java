package com.help.loan.distribute.service.api;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.model.DispatcheRecPO;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.LoanAmountUtil;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

public interface ApiSender {


    Logger LOGGER = LoggerFactory.getLogger(NBJiuxinApi.class);

    String msgModel = "城市-s%，s%：结果：s%";

    String Heng = "-";

    RestTemplate restTemplate = new RestTemplate();


    Random random = new Random(5);


    SendResult send(UserAptitudePO po, UserDTO select);



    default String getMedia(String channel){
        String media;
        if(StringUtils.isBlank(channel)){
            media = "头条";
        }else{
            if(channel.contains("ttt")){
                media ="头条";
            }else if(channel.contains("baidu")){
                media = "百度";
            }else if(channel.contains("weibo")){
                media = "微博";
            }else if(channel.contains("360")){
                media = "360";
            }else if(channel.contains("gdt")){
                media = "朋友圈";
            }else{
                media = "朋友圈";
            }
        }
        if(JudgeUtil.contain(channel,"xinxiangrong")){
            media  = "其他";
        }
        return media;
    }

    default String parseAccount(String channel){
        String media = "头条";
        String accountName = "左心房";
        if(StringUtils.isBlank(channel)){
            return media+"-"+accountName;
        }
        if(JudgeUtil.contain(channel,"ttt","sheng")){
            media = "头条";
        }
        if(JudgeUtil.contain(channel,"sheng")){
            accountName = "大额花钱";
        }
        if(JudgeUtil.contain(channel,"zxf")){
            accountName = "左心房";
        }
        if(JudgeUtil.contain(channel,"moerlong")){
            accountName = "摩尔龙";
        }
        return media+Heng+accountName;
    }

    default String getLoanType(Byte type){
        String loanType=Heng;
        if(JudgeUtil.in(type, DistributeConstant.LoanType.HOUSE)){
            loanType += "房抵";
        }else if(JudgeUtil.in(type,DistributeConstant.LoanType.CAR)){
            loanType += "车抵";
        }else if(JudgeUtil.in(type,DistributeConstant.LoanType.FUND)){
            loanType += "公积金";
        }else{
            loanType="";
        }
        return  loanType;
    }

    /**
     * 判断是否有可贷点
     * @param po  UserAptitudePO
     * @return boolean：true-有可贷点，false-无可贷点
     */
    default boolean isHaveAptitude(UserAptitudePO po){

        if(po.getPublicFund().contains("有，"))
            return true;
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            return true;
        if(JudgeUtil.in(po.getCar(),1,2))
            return true;
        if(JudgeUtil.in(po.getHouse(),1,2))
            return true;
        if(JudgeUtil.in(po.getInsurance(),1,2))
            return true;
        if(JudgeUtil.in(po.getCompany(),1))
            return true;
        int index = random.nextInt(5);
        switch (index){
            case 0:
                po.setPublicFund("有，个人月缴800元以上");
                break;
            case 1:
                po.setHouse(1);
                break;
            case 2:
                po.setCar(1);
                break;
            case 3:
                po.setInsurance(1);
                break;
            case 4:
                po.setCompany(1);
                break;
            default:po.setGetwayIncome(1);break;
        }
        return false;
    }


    default String getContent(UserAptitudePO po){
        StringBuffer content = new StringBuffer();
        content.append("品牌产品：").append(parseAccount(po.getChannel())).append("-").append(getLoanType(po.getType())).append("，");
        content.append("需求金额：").append(LoanAmountUtil.transform(po.getLoanAmount())).append("，");
        if(po.getPublicFund().contains("有，"))
            content.append(po.getPublicFund()).append("，");
        if(JudgeUtil.in(po.getHouse(),1,2))
            content.append("有本地商品房").append("，");
        if(JudgeUtil.in(po.getGetwayIncome(),1,2))
            content.append("有银行代发工资").append("，");
        if(JudgeUtil.in(po.getCar(),1,2))
            content.append("有车").append("，");
        if(JudgeUtil.in(po.getInsurance(),1,2))
            content.append("有商业保单").append("，");
        if(1 == po.getCompany())
            content.append("有公司营业执照").append("，");
        String str = content.toString();
        if(str.endsWith("，")){
            str = str.substring(0,str.length()-1);
        }
        content.append(po.getExtension());
        return str;
    }





    default DispatcheRecPO getDispatcheRecPO(Long orgId, Long custId, Integer status, String result) {
        DispatcheRecPO po = new DispatcheRecPO();
        Date date = new Date();
        po.setCreateDate(date);
        po.setUpdateDate(date);
        po.setCustomerId(custId);
        po.setOrgId(orgId);
        po.setDispatchStatus(status);
        po.setDispatchResult(result);
        return po;
    }

}
