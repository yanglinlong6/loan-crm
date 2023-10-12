package com.loan.cps.controller;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.common.JSONUtil;
import com.loan.cps.common.MobileLocationUtil;
import com.loan.cps.common.R;
import com.loan.cps.service.credit.CreditParam;
import com.loan.cps.service.credit.CreditService;
import com.loan.cps.service.credit.CreditUserPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;

@RestController
public class CreditController {

    private static final Logger LOG = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    private CreditService creditService;

    @Autowired
    private RedisTemplate<String, Object> template;

    /**
     * 信用卡逾期接口
     * @param
     * @return
     */
    @RequestMapping("/credit/apply")
    @ResponseBody
    public R applyCreditCart(@RequestBody() CreditUserPO creditUserPO){
        LOG.info("[信用逾期1]获客：{}",creditUserPO);
        // 测试
        if("00000".equals(creditUserPO.getMsgCode())){
            JSONObject andCheck = MobileLocationUtil.getAndCheck(creditUserPO.getMobile());
            if(andCheck!=null) {
                String city = andCheck.getString("City");
                creditUserPO.setCity(city.endsWith("市")?city:city+"市");
            }
            R  r = creditService.addCreditUser(creditUserPO);
            return r;
        }
        // 正式
        String redisCode = (String)template.opsForValue().get(CustController.MOBILE_CODE_REDIS_KEY_PREFIX+creditUserPO.getMobile());
        if(redisCode == null ) {
            return R.fail("1","验证码已过期，请重新发送");
        }
        if(!redisCode.equals(creditUserPO.getMsgCode())) {
            return R.fail("1","验证码错误");
        }
        JSONObject andCheck = MobileLocationUtil.getAndCheck(creditUserPO.getMobile());
        if(andCheck!=null) {
            String city = andCheck.getString("City");
            creditUserPO.setCity(city.endsWith("市")?city:city+"市");
        }
        R  r = creditService.addCreditUser(creditUserPO);
        return r;
    }

    @RequestMapping("/credit/apply2")
    @ResponseBody
    public R applyCreditCart2(@RequestBody() CreditUserPO po, HttpServletRequest request){
        LOG.info("[信用逾期2]获客：{}",po);
        if(StringUtils.isEmpty(po.getMobile())){
            po.setMobile(po.getTelphone());
        }
        JSONObject andCheck = MobileLocationUtil.getAndCheck(po.getMobile());
        if(andCheck != null) {
            String city = andCheck.getString("City");
            if(!StringUtils.isEmpty(city)){
                po.setCity(city.endsWith("市")?city:city+"市");
            }else{
                po.setCity(AptitudeUtils.getIpCity(request));
            }
        }
        if(!StringUtils.isEmpty(po.getForm_remark())){
            JSONObject formRemark = JSONUtil.toJSON(po.getForm_remark());
            po.setAmount(formRemark.getString("逾期金额"));
            po.setOverdue(formRemark.getString("逾期时间"));
        }
        if(!StringUtils.isEmpty(po.getRemark_dict())){
            JSONObject remarkDict = JSONUtil.toJSON(po.getRemark_dict());
            po.setAmount(remarkDict.getString("逾期金额"));
            po.setOverdue(remarkDict.getString("逾期时间"));
        }
        po.setChannel(po.getChannel()+"-"+po.getUcid()+"-"+po.getClueId());
        R  r = creditService.addCreditUser2(po);
        LOG.info("[信用逾期2]获客：{}-{}",po,r);
        return r;
    }

    @RequestMapping("/credit/param")
    @ResponseBody
    public R creditParam(){
        R r = R.ok();
        JSONObject data = new JSONObject();
        data.put("question", CreditParam.questions);
        data.put("history",CreditParam.getHistory());
        r.setData(data);
        return r;
    }

    @RequestMapping("/credit/send")
    @ResponseBody
    public R creditSend(){
        R r = R.ok();

        return r;
    }
}
