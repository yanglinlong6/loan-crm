package com.help.loan.distribute.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.*;
import com.help.loan.distribute.config.AppContextUtil;
import com.help.loan.distribute.service.api.ApiSender;
import com.help.loan.distribute.service.api.SendResult;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.MainCity;
import com.help.loan.distribute.service.cache.CacheService;
import com.help.loan.distribute.service.cityAptitude.dao.CityAptitudeRatePOMapper;
import com.help.loan.distribute.service.org.OrgService;
import com.help.loan.distribute.service.org.model.OrgBO;
import com.help.loan.distribute.service.session.SessionService;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.dao.WechatUserBindDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import com.help.loan.distribute.util.DESUtil;
import com.help.loan.distribute.util.DisConstant;
import com.loan.cps.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
public class CpaScheduleApi implements CpaSchedule {


    private static Logger LOG = LoggerFactory.getLogger(CpaScheduleApi.class);

    @Autowired
    UserAptitudeDao dao;

    @Autowired
    WechatUserBindDao wechatUserBindDao;

    @Autowired
    SessionService sessionService;

    @Autowired
    OrgService orgService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private String ORG_DISTRIBUTE_TASK_10_KEY = "org_distribute_task_10_key";
    private String ORG_DISTRIBUTE_TASK_30_KEY = "org_distribute_task_30_key";

//    static Map<Long,String> orgZDTMap=new HashMap<>();
//
//    static {
//    	orgZDTMap.put(10001l, "LM");
//    	orgZDTMap.put(10002l, "LM");
//    	orgZDTMap.put(10003l, "LM");
//    	orgZDTMap.put(10004l, "JJD");
//    	orgZDTMap.put(10005l, "JJD");
//    	orgZDTMap.put(10006l, "YSN");
//    	orgZDTMap.put(10008l, "SDZX");
//    	orgZDTMap.put(10011l, "QDD");
//    	orgZDTMap.put(10013l, "XW");
//    	orgZDTMap.put(10014l, "JJXN");
//    	orgZDTMap.put(10012l, "ZDJR");
//        orgZDTMap.put(10020l, "XW"); // 信e贷
//        orgZDTMap.put(10025l, "XX");
//        orgZDTMap.put(10018l, "QE");
//    }


    //    @Scheduled(cron = "0/30 * * * * ?")
    @Override
    public void send() {
        try {
            if (!stringRedisTemplate.opsForValue().setIfAbsent(ORG_DISTRIBUTE_TASK_10_KEY, "1", 100, TimeUnit.SECONDS)) {
                LOG.error("30秒钟分发定时任务正在实行，直接返回。。。。。");
                return;
            }
            LOG.info("30秒钟分发定时任务开始实行");
            send1();
        } catch (Exception e) {
            stringRedisTemplate.opsForValue().getOperations().delete(ORG_DISTRIBUTE_TASK_10_KEY);
            LOG.error(e.getMessage(), e);
        }
    }

    public void send1() {
        try {
            List<UserAptitudePO> byLevel = dao.getByLevel(4);
            for (UserAptitudePO po : byLevel) {
                try {
                    LOG.info("发送客户:{}", po);
                    if (JudgeUtil.in(po.getName(), "测试", "test", "*") || JudgeUtil.in(po.getMobile(), "*")) {
                        updateUserAptitude(6, po.getUserId());
                        continue;
                    }
                    if (StringUtils.isBlank(po.getProvince()) || JudgeUtil.in(po.getProvince(), "0", "1"))
                        send2(po);
                    else
                        updateUserAptitude(11, po.getUserId());//11-表示待确认空号再分发
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);// 单个用户分发异常，记录日志，继续下一个用户分发
                    continue;
                }
            }
            stringRedisTemplate.opsForValue().getOperations().delete(ORG_DISTRIBUTE_TASK_10_KEY);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        } finally {
            stringRedisTemplate.opsForValue().getOperations().delete(ORG_DISTRIBUTE_TASK_10_KEY);
        }
    }

    public Long send2(UserAptitudePO po) {
//        if(StringUtils.isBlank(po.getChannel()) && (po.getUpdateDate().getTime() + 5 * 60 * 1000) > System.currentTimeMillis()) {
//            LOG.info("粉丝用户:{}-{},更新时间:{}[5分钟之内不分发]", po.getMobile(), po.getCity(), DateUtil.formatToString(po.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
//            return null;
//        }
        UserDTO select = wechatUserBindDao.select(po.getUserId());
        if (null != select)
            po.setWxType(select.getWxType());
        boolean isExclusive = false;
        Integer chooseCount = 0;
        Long orgId = orgService.chooseExclusiveOrg(po, select);//挑选专属机构发送
        if (null != orgId) {//首先找是否是专属公众号机构  如果专属机构发送成功，则返回orgId，如果是空的说明没发送成功，继续发送
            updateUserAptitude(5, po.getUserId());
            return orgId;
        }
        List<OrgBO> orgList = orgService.chooseOrg(po, chooseCount);
        if (CollectionUtil.isEmpty(orgList)) {
            LOG.info("用户号码:{},公众号:{},挑选非专属机构【没有符合的机构】", po.getMobile(), po.getWxType());
            if (isExclusive) {
                updateUserAptitude(6, po.getUserId());
            } else {
                //如果是主要城市,则保留,否则发送给
                if (MainCity.CITY.contains(po.getCity()) || JudgeUtil.in(po.getCreateBy(), DisConstant.User.Type.CREDIT)) {
                    updateUserAptitude(9, po.getUserId());
                } else {
                    updateUserAptitude(99, po.getUserId());
                }
            }
            return null;
        }
        boolean isSuccess = false;
        orgId = 0l;
        int index = 0;
        for (OrgBO orgBO : orgList) {
            if (index > 0) {
                po.setIsRepeat(true);
                if (!orgBO.checkRepeatCount(cityAptitudeRatePOMapper, po).getCheckPass()) {
                    continue;
                }
            }
            SendResult send = distribute(orgBO, po, select);
            if (send.isSuccess()) {
                isSuccess = true;
                orgId = orgBO.getOrgId();
                break;
            }
            if (!JudgeUtil.in(po.getCreateBy(), DisConstant.User.Type.CAR, DisConstant.User.Type.HOUSE))
                index++;
        }
        if (isSuccess) {
            updateUserAptitude(5, po.getUserId(), po.getIsRepeat(), (null == orgId ? "" : orgId.toString()), po);
        } else {
            int distributeTime = Integer.valueOf(DateUtil.formatToString(new Date(), "HH")).intValue();
            if (distributeTime < 8 || distributeTime > 22) {
                updateUserAptitude(9, po.getUserId());
            } else
                updateUserAptitude(6, po.getUserId());
        }
        return orgId;
    }

    @Autowired
    CityAptitudeRatePOMapper cityAptitudeRatePOMapper;

    @Autowired
    CacheService cacheService;

    public JSONObject send5(UserAptitudePO po) {
        if (StringUtils.isBlank(po.getChannel()) && (po.getUpdateDate().getTime() + 5 * 60 * 1000) > System.currentTimeMillis()) {
            LOG.info("粉丝用户:{}-{},更新时间:{}[5分钟之内不分发]", po.getMobile(), po.getCity(), DateUtil.formatToString(po.getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
            return null;
        }
        LOG.info("send rec API = " + JSON.toJSONString(po));
        UserDTO select = wechatUserBindDao.select(po.getUserId());
        if (null != select)
            po.setWxType(select.getWxType());
        boolean isExclusive = false;
        Integer chooseCount = 0;
        JSONObject rObj = new JSONObject();
        rObj.put("isSuccess", 2);//表示提送重复失败
        rObj.put("orgId", "");

        //专属机构发送
        Long orgId = orgService.chooseExclusiveOrg(po, select);
        LOG.info("chooseExclusiveOrg orgId==" + orgId);
        if (null != orgId) {
            updateUserAptitude(5, po.getUserId());
            rObj.put("isSuccess", 1); // 标识推送成功
            rObj.put("orgId", orgId);
            return rObj;
        }

        // 到这里表示: 没有专属机构接收,或者  非专属客户
        List<OrgBO> orgList = orgService.chooseOrg(po, chooseCount);
        LOG.info("chooseOrg orgList==" + JSONUtil.toJsonString(orgList));
        if (CollectionUtil.isEmpty(orgList)) {
            LOG.info("用户号码:{},公众号:{},挑选非专属机构【没有符合的机构】", po.getMobile(), po.getWxType());
            if (isExclusive || JudgeUtil.contain(po.getChannel(), "shengbei")) { // 省呗如果没有挑选到机构,则标记为失败
                updateUserAptitude(6, po);
            } else {
                //如果是主要城市,则保留,否则发送给
//                String advertisingCity  = cacheService.getValue("advertising","city");;
                if (MainCity.CITY.contains(po.getCity())
                        || JudgeUtil.in(po.getCreateBy(), DisConstant.User.Type.CREDIT, DisConstant.User.Type.CAR, DisConstant.User.Type.HOUSE)) {
                    updateUserAptitude(9, po.getUserId());
                } else {
                    updateUserAptitude(99, po.getUserId());
                }
            }
            rObj.put("isSuccess", 3); // 表示客户没有挑选到客户
            return rObj;
        }

        boolean isSuccess = false;
        int index = 0;
        String id = "";
        for (OrgBO orgBO : orgList) {
            LOG.info("用户:{}-{},第{}次发送", po.getName(), po.getMobile(), index);
            if (index > 0) {
                po.setIsRepeat(true);
                if (!orgBO.checkRepeatCount(cityAptitudeRatePOMapper, po).getCheckPass()) {
                    continue;
                }
            }
            SendResult send = distribute(orgBO, po, select);
            if (send.isSuccess()) {
                isSuccess = true;
                rObj.put("isSuccess", 1); // 标识推送成功
                id = orgBO.getOrgId().toString();
                break;
            }
            index++;
        }
        try {
            if (isSuccess) {
                updateUserAptitude(5, po.getUserId(), po.getIsRepeat(), id, po);
            } else {
                int distributeTime = Integer.valueOf(DateUtil.formatToString(new Date(), "HH")).intValue();
                if (distributeTime < 8 || distributeTime > 22) {
                    updateUserAptitude(9, po.getUserId());
                } else
                    updateUserAptitude(6, po.getUserId());
            }
            rObj.put("orgId", id);
        } catch (Exception e) {
            LOG.error("更新用户状态异常:{}", e.getMessage(), e);
            if (isSuccess) {
                updateUserAptitude(5, po.getUserId(), po.getIsRepeat(), id, po);
            }
            rObj.put("orgId", id);
        }
        return rObj;
    }

    /**
     * 分发
     */
    private SendResult distribute(OrgBO org, UserAptitudePO po, UserDTO select) {
        po.setOrgId(org.getOrgId());
        LOG.info("用户号码:{},公众号:{},挑选到机构：{}-{}", po.getMobile(), po.getWxType(), org.getOrgId(), org.getOrgName());
        ApiSender sender = AppContextUtil.getBean("apiSender_" + org.getOrgId(), ApiSender.class);
        SendResult send = sender.send(po, select);
        LOG.info("用户id:{}-{},挑选非专属机构:{}-{},分发结果：【{}-{}】,是否是排重数据:{}", po.getUserId(), po.getMobile(), org.getOrgId(), org.getOrgName(), send.isSuccess(), send.getResultMsg(), po.getIsRepeat());
        return send;
    }

    /**
     * 发送固定客服消息
     *
     * @param org OrgBO
     */
    private void sendCustomerMsg(String mobile, UserAptitudePO po, OrgBO org, UserDTO user) {
        try {
            if (null == user)
                return;
            JSONObject custmerMsg = new JSONObject();
            custmerMsg.put("msgtype", WechatConstants.MsgConstants.WX_CUST_MSG_TYPE_TEXT);
            custmerMsg.put("touser", po.getUserId());
            JSONObject text = new JSONObject();
            text.put("content", "恭喜您成功到达这一步\n您的银行额度已通过\n稍后会有工作人员与您联系放款事宜，请保持手机畅通\n风控人员审核，还请积极配合！\n敬请期待\n\n" +
                    "我们已经成功帮助过1000万以上客户从银行借到钱，拥有国内832家银行专属通道，1000多款银行产品，累计放款50亿。\n为客户节省上千万利息费，没有下不了的款哦");
            custmerMsg.put("text", text);
            JSONObject wechat = JSON.parseObject(WechatCenterUtil.getWechat("", "", po.getUserId())).getJSONObject("o");
            String wechatId = wechat.getString("wechatId");
            String result = WechatCenterUtil.sendCustMsg(custmerMsg, wechatId, "", "");
            LOG.info("用户-{}，申请-{}，发送客服消息结果：-{}", mobile, org.getOrgNickname(), result);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void updateUserAptitude(Integer level, String userId) {
        UserAptitudePO userAptitudePO = new UserAptitudePO();
        userAptitudePO.setLevel(level);
        userAptitudePO.setUserId(userId);
        dao.update(userAptitudePO);
    }

    @Transactional
    public void updateUserAptitude(Integer level, UserAptitudePO po) {
        UserAptitudePO userAptitudePO = new UserAptitudePO();
        userAptitudePO.setLevel(level);
        userAptitudePO.setUserId(po.getUserId());
        if (JudgeUtil.in(level, 6)) {
            userAptitudePO.setName(encryptName(po.getName()));
            userAptitudePO.setMobile(encryptMobile(po.getMobile()));
        }
        LOG.info("更新客户信息:{}", userAptitudePO.toString());
        dao.update(userAptitudePO);
    }


    @Transactional
    public void updateUserAptitude(Integer level, String userId, boolean isRepeat, String orgId, UserAptitudePO po) {
        UserAptitudePO userAptitudePO = new UserAptitudePO();
        userAptitudePO.setLevel(level);
        userAptitudePO.setUserId(userId);
        if (isRepeat)
            userAptitudePO.setUpdateBy(orgId);
        if (JudgeUtil.in(level, 5)) {// 5标识成功, 6标识失败
            userAptitudePO.setName(encryptName(po.getName()));
            userAptitudePO.setMobile(encryptMobile(po.getMobile()));
        }
        dao.update(userAptitudePO);
    }

    public static String encryptName(String name) {
        if (StringUtils.isBlank(name))
            return name;
        if (name.length() <= 2) {
            return name.substring(0, 1) + "*";
        }
        String one = name.substring(0, 1);
        String two = name.substring(name.length() - 1, name.length());
        return one + "*" + two;
    }

    /**
     * 加密手机号码
     *
     * @param mobile
     * @return
     */
    public static String encryptMobile(String mobile) {
        if (StringUtils.isBlank(mobile)) {
            return mobile;
        }
        int length = mobile.length();
        String one = mobile.substring(0, 3);
        String two = mobile.substring(length - 4, length);
        return one + "****" + two;
    }


    //	@Scheduled(fixedRate = 30*1000)
//    @Scheduled(cron = "0 0/5 9-23 * * ?")
    public void send3() {
        if (!stringRedisTemplate.opsForValue().setIfAbsent(ORG_DISTRIBUTE_TASK_30_KEY, "1", 120, TimeUnit.SECONDS)) {
            LOG.error("30秒分发定时任务正在实行,直接返回。。。。。");
            return;
        }
        send4();
    }

    public void send4() {
        try {
            List<UserAptitudePO> byLevel = dao.getByLevel(2);
            for (UserAptitudePO po : byLevel) {
                Session session = null;
                try {
                    session = sessionService.getSession(po.getUserId());
                    if (session != null && System.currentTimeMillis() - session.getUpstream_time() < 65 * 60 * 1000) {
                        continue;
                    }
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                }
                send2(po);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            stringRedisTemplate.opsForValue().getOperations().delete(ORG_DISTRIBUTE_TASK_30_KEY);
        } finally {
            stringRedisTemplate.opsForValue().getOperations().delete(ORG_DISTRIBUTE_TASK_30_KEY);
        }
    }


    public static void main(String[] args) {
        String str = "13632965527";
        String one = str.substring(0, 3);
        String two = str.substring(str.length() - 4, str.length());
        System.out.println(one + "****" + two);
    }
}
