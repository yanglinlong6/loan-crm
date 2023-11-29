package com.loan.cps.controller;

import com.alibaba.fastjson.JSON;
import com.loan.cps.common.*;
import com.loan.cps.dao.UserAptitudeDao;
import com.loan.cps.entity.UserAptitudePO;
import com.loan.cps.entity.ZxfCustomerBO;
import com.loan.cps.service.UserAptitudeService;
import com.loan.cps.service.cache.CacheService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerApplyController {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerApplyController.class);

    @Autowired
    UserAptitudeService aptService;

    @Autowired
    CacheService cacheService;
    @Autowired
    UserAptitudeDao dao;


    /**
     * 省呗接口
     *
     * @param po UserAptitudePO
     * @return R apply
     */
    @RequestMapping(value = "/customer/apply/shengbei")
    @ResponseBody
    public R apply(@RequestBody UserAptitudePO po) {
        LOG.info("[省呗]创建用户 " + JSON.toJSONString(po));
        R ok = R.ok();
        try {
            if (null == po || !JudgeUtil.in(po.getChannel(), "shengbei-001")) {
                ok = R.fail("非法渠道");
                return ok;
            }
            String value = cacheService.getValue(CPSConstant.Redis.Advertising.FIELD, CPSConstant.Redis.Advertising.CITY);
            if (!JudgeUtil.in(po.getCity(), value.split(","))) {
                ok = R.fail("城市不符");
            } else {
                AptitudeUtils.setIpCity(po, null);
                AptitudeUtils.setMobileCity(po);
                ok = aptService.addShengBei(po);
            }
        } catch (Exception e) {
            ok.setCode("1");
            ok.setMsg(e.getMessage());
        }
        LOG.info("[省呗]推送结果:{}", ok.toString());
        return ok;
    }

    /**
     * 左心房接口
     *
     * @param customer ZxfCustomerBO
     * @return R
     */
    @RequestMapping(value = "/customer/apply/zxf")
    @ResponseBody
    public ZxfR applyWithZXF(@RequestBody ZxfCustomerBO customer) {
        LOG.info("[左心房]创建用户:" + JSONUtil.toString(customer));
        ZxfR zxfR = ZxfR.ok();
        try {
            if (null == customer) {
                zxfR.setCode("201");
                zxfR = ZxfR.fail("未提交客户信息");
                return zxfR;
            }
//            String value = cacheService.getValue(CPSConstant.Redis.Advertising.FIELD,CPSConstant.Redis.Advertising.CITY);
//            if(!JudgeUtil.in(customer.getLocation(),value.split(","))){
//                ok = R.fail("城市不符");
//            }
//            String remark = customer.getRemark();
//            if(StringUtils.isBlank(remark)){
//                ok = R.fail("客户未提交资质");
//            }
            UserAptitudePO user = new UserAptitudePO();
            user.setName(customer.getName());
            user.setMobile(customer.getMobile());
            user.setMd5(MD5Util.getMd5String(customer.getMobile()));
            String city = customer.getLocation();
            if (StringUtils.isBlank(city) || JudgeUtil.contains(city, "未知")) {
                city = customer.getCustomerLocation();
                if (city.contains("-")) {
                    city = city.split("-")[1];
                }
                if (!city.endsWith("市")) {
                    city = city + "市";
                }
                user.setCity(city);
            } else {
                if (JudgeUtil.contains(city, "-")) {
                    city = city.split("-")[1];
                }
                if (!city.endsWith("市")) {
                    city = city + "市";
                }
                user.setCity(city);
            }
            user.setLoanAmount(customer.getLoanAmount());
            user.setAge(customer.getAge());
            user.setGender("男".equals(customer.getSex()) ? 1 : 2);

            UserAptitudePO byMD5 = dao.getByMobileMD5(user.getMd5());
            if (null != byMD5) {
                return ZxfR.fail("202", "您的申请已提交,请勿重复申请", false);
            }

            if (null == user.getAge() || user.getAge() < 25) {
                user.setAge(25);
            }

            user.setMobileLocation(AptitudeUtils.getMobileCity(user.getMobile()));
            user.setIpLocation(AptitudeUtils.getIpCity(customer.getIp()));
            String loanType = customer.getCusTypeStr();
            if (JudgeUtil.in(loanType, "车抵")) {
                user.setType(CPSConstant.ProductType.Car);
                user.setExtension(customer.getRemark());
            } else if (JudgeUtil.in(loanType, "房抵")) {
                user.setType(CPSConstant.ProductType.House);
                user.setExtension(customer.getRemark());
            } else {
                user.setType(CPSConstant.ProductType.Credit);
                String remark = customer.getRemark();
                if (StringUtils.isNotBlank(remark)) {
                    if (JudgeUtil.contains(remark, "有房")) {
                        user.setHouse(1);
                    } else user.setHouse(0);

                    if (JudgeUtil.contains(remark, "有私家车")) {
                        user.setCar(1);
                    } else user.setCar(0);

                    if (JudgeUtil.contains(remark, "有保单", "保险")) {
                        user.setInsurance(1);
                    } else user.setInsurance(0);

                    if (JudgeUtil.contains(remark, "公积金")) {
                        user.setPublicFund("有，个人月缴300-800元");
                    } else user.setPublicFund("无");

                    if (JudgeUtil.contains(remark, "银行代发", "有工资")) {
                        user.setGetwayIncome(1);
                    } else user.setGetwayIncome(0);

                    if (JudgeUtil.contains(remark, "营业执照", "企业税")) {
                        user.setCompany(1);
                    } else user.setCompany(0);
                }
                user.setExtension(remark);
            }
            user.setChannel("zxf-ttt-001");
            R ok = aptService.add(user);
            if (R.SUC.equals(ok.getCode())) {
                zxfR.setCode("200");
                zxfR.setMessage("操作成功");
                zxfR.setData("success");
            } else {
                zxfR.setCode(ok.getCode());
                zxfR.setMessage(ok.getMsg());
            }
        } catch (Exception e) {
            LOG.error("[左心房]客户信息处理异常:{}", e.getMessage(), e);
            zxfR.setCode("201");
            zxfR.setMessage(e.getMessage());
        }
        LOG.info("[左心房]推送结果:{}", zxfR);
        return zxfR;
    }

}
