package com.help.loan.distribute.service.org;

import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.config.AppContextUtil;
import com.help.loan.distribute.service.api.ApiSender;
import com.help.loan.distribute.service.api.SendResult;
import com.help.loan.distribute.service.api.dao.DispatcheRecDao;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.api.utils.MainCity;
import com.help.loan.distribute.service.api.utils.TouTiaoChannel;
import com.help.loan.distribute.service.cityAptitude.CityAptitudeService;
import com.help.loan.distribute.service.cityAptitude.dao.CityAptitudeRatePOMapper;
import com.help.loan.distribute.service.org.dao.OrgDao;
import com.help.loan.distribute.service.org.model.OrgAptitudePO;
import com.help.loan.distribute.service.org.model.OrgBO;
import com.help.loan.distribute.service.org.model.OrgDistributeStatisticsBO;
import com.help.loan.distribute.service.org.model.OrgPO;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrgServiceImpl implements OrgService {

    private final static Logger log = LoggerFactory.getLogger(OrgServiceImpl.class);

    /**
     * 机构数据分发：机构列表缓存key：distribute.org.list.key
     */
    private static final String DISTRIBUTE_ORG_LIST_KEY = "distribute.org.list.key";

    private static final String DISTRIBUTE_ORG_LIST_EXCLUSIVE_KEY = "distribute.org.list.exclusive.key";

    /**
     * 机构数据分发：机构列表当前机构下标缓存key：distribute.org.list.index.key
     */
    private static final String DISTRIBUTE_ORG_LIST_INDEX_KEY = "distribute.org.list.index.key";

    @Autowired
    private OrgDao orgDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserAptitudeDao userAptitudeDao;

    @Autowired
    private CityAptitudeService cityAptitudeService;

    @Autowired
    private DispatcheRecDao dispatcheRecDao;


    @Override
    public List<OrgBO> getCacheAll(Boolean isExclusive) {

        String key;
        if (isExclusive) {
            key = DISTRIBUTE_ORG_LIST_EXCLUSIVE_KEY; // 专属公众号机构缓存key
        } else {
            key = DISTRIBUTE_ORG_LIST_KEY; // 非专属公众号机构列表缓存key
        }
        Object value = stringRedisTemplate.opsForValue().get(key);
        List<OrgBO> orgBOList = new ArrayList<>();
        String distributeDate = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        if (null != value && value.toString().length() > 6) {
            orgBOList.addAll(JSONUtil.toJavaBeanList(value.toString(), OrgBO.class));
            if (distributeDate.equals(orgBOList.get(0).getDistributeDate().trim())) {
                return orgBOList;
            }
            log.info("当前日期:{},缓存日期:{}-[更新缓存]", distributeDate, orgBOList.get(0).getDistributeDate());
            orgBOList.clear();
            stringRedisTemplate.opsForValue().getOperations().delete(key);
            orgBOList.addAll(getAll(isExclusive));
            if (orgBOList.isEmpty()) {
                return orgBOList;
            }
            orgBOList = setDistributeDate(orgBOList, distributeDate);
            // 缓存
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonString(orgBOList), 20, TimeUnit.SECONDS);
            return orgBOList;
        }
        log.info("当前日期:{}-[缓存失效.....]", distributeDate);
        orgBOList.addAll(getAll(isExclusive));
        if (orgBOList.isEmpty()) {
            return orgBOList;
        }
        orgBOList = setDistributeDate(orgBOList, distributeDate);
        // 缓存
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonString(orgBOList), 20, TimeUnit.SECONDS);
        log.info("getCacheAll orgBOList==" + JSONUtil.toJsonString(orgBOList));
        return orgBOList;
    }


    private List<OrgBO> setDistributeDate(List<OrgBO> orgBOList, String distributeDate) {
        if (orgBOList.isEmpty()) {
            return orgBOList;
        }
        for (OrgBO org : orgBOList) {
            org.setDistributeDate(distributeDate);
        }
        return orgBOList;
    }

    @Override
    public List<OrgBO> getAll(Boolean isExclusive) {

        synchronized (this) {
            String key;
            if (isExclusive)
                key = DISTRIBUTE_ORG_LIST_EXCLUSIVE_KEY; // 专属公众号机构缓存key
            else key = DISTRIBUTE_ORG_LIST_KEY; // 非专属公众号机构列表缓存key
            Object value = stringRedisTemplate.opsForValue().get(key);
            if (null != value && JSONUtil.isJsonString(value.toString())) {
                List<OrgBO> orgBOList = JSONUtil.toJavaBeanList(value.toString(), OrgBO.class);
                if (!CollectionUtil.isEmpty(orgBOList)) {
                    return orgBOList;
                }
            }
            List<OrgPO> list = orgDao.selectAllOrg();
            if (CollectionUtil.isEmpty(list)) {
                return null;
            }
            log.info("查询机构列表......start");
            List<OrgBO> orgBOList = new ArrayList<>();
            for (OrgPO org : list) {
                List<OrgAptitudePO> aptitudeList = orgDao.selectOrgAptitude(org.getOrgId());
                if (CollectionUtil.isEmpty(aptitudeList)) {
                    log.error("机构【{}】，orgId:{}没有配置分发限制条件", org.getOrgName(), org.getOrgId());
                    continue;
                }
                for (OrgAptitudePO orgAptitude : aptitudeList) {
//                    log.info("机构-{}-{}，配量信息:{}",org.getId(),org.getOrgNickname(),orgAptitude.toString());
                    OrgBO orgBO = JSONUtil.toJavaBean(JSONUtil.toJsonString(org), OrgBO.class);
                    orgBO.setOrgAptitude(orgAptitude);
                    String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
                    String startDate = date + " 00:00:00";
                    String endDate = date + " 23:59:59";
                    if (isExclusive) {// 专属公众号机构
                        if (StringUtils.isNotBlank(orgAptitude.getWechat()) || StringUtils.isNotBlank(orgAptitude.getChannel())) {
                            orgBO.setDistributeCount(orgDao.selectTodayDistributeSuccessCount(orgBO.getOrgId(), startDate, endDate));
                            orgBOList.add(orgBO);
                        }
                        continue;
                    }
                    // 到这里说明不是专属账户或者专属渠道，如果配量是专属账户或者专属渠道，则不分发
                    if (StringUtils.isNotBlank(orgAptitude.getWechat()) || StringUtils.isNotBlank(orgAptitude.getChannel())) {
                        log.info("机构-{}-{}-{}，配量是专属账户获取渠道，不分发", org.getOrgId(), org.getOrgNickname(), orgAptitude.getChannel());
                        continue;
                    }
                    //如果城市是空，則表示不限制城市
                    if (StringUtils.isBlank(orgAptitude.getCity()) || orgAptitude.getCity().split(",").length > 1) {
                        //判断是否超过当日流量限制，如果超过则跳过
                        Integer count = orgDao.selectTodayDistributeSuccessCount(orgBO.getOrgId(), startDate, endDate);
                        if (null == count)
                            count = 0;
                        if (count.intValue() >= orgAptitude.getLimitCount()) {
                            log.info("机构:{}-{},不限城市,限量:{},已发送量:{}【超量】", org.getOrgId(), org.getOrgName(), orgAptitude.getLimitCount(), count);
                            continue;
                        }
                        orgBO.setDistributeCount(count);
                        // 设置机构城市可贷点参数
                        orgBO.setCityAptitudeRate(cityAptitudeService.getCityAptitudeRate(orgBO.getOrgId(), orgAptitude.getCity(), startDate, endDate));
                        orgBOList.add(orgBO);
                        continue;
                    }
                    //判断是否超过当日流量限制，如果超过则跳过
                    Integer count = orgDao.selectTodayDistributeSuccessCount2(orgBO.getOrgId(), orgAptitude.getCity(), startDate, endDate);
                    if (count.intValue() >= orgAptitude.getLimitCount().intValue()) {
                        log.info("机构:{}-{},城市:{},限量:{},已发送量:{}【超量】", org.getOrgId(), org.getOrgName(), orgAptitude.getCity(), orgAptitude.getLimitCount(), count);
                        continue;
                    }
                    orgBO.setDistributeCount(count);
                    orgBO.setDistributeRepeatCount(orgDao.selectTodayDistributeRepeatCount(orgBO.getOrgId().toString(), orgAptitude.getCity(), startDate, endDate)); //设置机构重复数量
                    // 设置机构城市参数
                    orgBO.setCityAptitudeRate(cityAptitudeService.getCityAptitudeRate(orgBO.getOrgId(), orgAptitude.getCity(), startDate, endDate));
                    orgBOList.add(orgBO);
                }
            }
            log.info("查询机构列表........end");
            return orgBOList;

        }
    }

    @Override
    public OrgBO getOrgBO(Long orgId) {
        OrgPO org = orgDao.selectOrg(orgId);
        if (null == org) {
            return null;
        }
        return JSONUtil.toJavaBean(JSONUtil.toJsonString(org), OrgBO.class);
    }

    /**
     * 选择专属公众号机构分发
     *
     * @param userAptitude UserAptitudePO
     * @return List<OrgBO>
     */
    public Long chooseExclusiveOrg(UserAptitudePO userAptitude, UserDTO select) {
        return chooseExclusiveOrg(getCacheAll(true), userAptitude, select);
    }

    @Override
    public List<OrgBO> chooseOrg(UserAptitudePO userAptitude, Integer chooseCount) {
        // 如果不是专属机构则走分发逻辑选择机构
        return singleOut(getCacheAll(false), userAptitude, chooseCount);
    }

    @Autowired
    private CityAptitudeRatePOMapper cityAptitudeRatePOMapper;


    private List<OrgBO> singleOut(List<OrgBO> orgBOList, UserAptitudePO userAptitude, Integer chooseCount) {
        log.info("singleOut:{}-{},第{}次选机构", userAptitude.getMobile(), userAptitude.getCity(), chooseCount);
        if (orgBOList.isEmpty()) {
            log.error("分发业务,选择机构机构[列表是空的]");
            return null;
        }
//        log.info("orgBOList==" + JSONUtil.toJsonString(orgBOList));
        List<OrgBO> newList = new ArrayList<>();
        List<OrgBO> lastList = new ArrayList<>();
        // 如果不是专属机构则走分发逻辑选择机构
        for (int i = 0; i < orgBOList.size(); i++) {
            OrgBO org = orgBOList.get(i);
            try {
                org.checkCreditType(userAptitude)
                        .checkChannel2(userAptitude)
                        .checkCity(userAptitude)
                        .checkDistributeTime(userAptitude)
                        .checkDistributeCount(orgDao)
                        .checkName(userAptitude, userAptitudeDao)
                        .checkScoreRate(cityAptitudeRatePOMapper, userAptitude);
//                        .checkRepeatCount(cityAptitudeRatePOMapper,userAptitude);
                if (!org.getCheckPass()) {
                    continue;
                }
                computeWeight(newList, lastList, org, userAptitude);
                //如果是头条渠道，则判断渠道是否与机构渠道匹配，如果匹配则加入可分发机构列表，否则不分发给该机构
//                if(JudgeUtil.in(userAptitude.getChannel(), TouTiaoChannel.CHANNELS)){
//                    log.info("客户是头条渠道来的,验证渠道-{}:客户渠道-{}, 机构渠道-{}",org.getOrgNickname(),userAptitude.getChannel(),org.getOrgAptitude().getChannel());
//                    org.checkChannel(userAptitude);
//                    if(org.getCheckPass()) {
//                        computeWeight(newList,lastList,org,userAptitude);
//                    }
//                    continue;
//                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        if (CollectionUtil.isEmpty(newList) && CollectionUtil.isEmpty(lastList))
            return null;
        if (JudgeUtil.in(getJudgeAptitudeForScore(userAptitude), "ordinary", "well")) {
            log.info("排序：根据机构【高分客户占比】升序排序");
            newList.sort((e1, e2) -> -e1.getWeight().compareTo(e2.getWeight()));
        } else {
            log.info("排序：根据机构【权重】降序排序");
            newList.sort((e1, e2) -> {
                int value = e1.getScore().compareTo(e2.getScore());
                if (value != 0)
                    return value;
                return -e1.getWeight().compareTo(e2.getWeight());
            });
        }
        newList.addAll(lastList);
        StringBuffer logContent = new StringBuffer();
        for (OrgBO org : newList) {
            logContent.append(org.getOrgName()).append(",");
        }
        log.info("用户：{}-{}-{}", userAptitude.getName(), userAptitude.getMobile(), logContent);
//        if(isRetain(newList,userAptitude)) {//true-保留，则不分发
//            log.info("用户：{}-{}-{}，【主要城市保留】",userAptitude.getName(),userAptitude.getMobile(),userAptitude.getCity());
//            return null;
//        }
        return newList;
    }


    /**
     * 计算机构城市权重
     *
     * @param newList      可分发机构列表
     * @param lastList     放在最后的机构列表
     * @param org          当前机构
     * @param userAptitude 用户
     */
    private void computeWeight(List<OrgBO> newList, List<OrgBO> lastList, OrgBO org, UserAptitudePO userAptitude) {
        BigDecimal value = org.getOrgAptitude().getSingleIncome().multiply(new BigDecimal((org.getOrgAptitude().getLimitCount() - org.getDistributeCount())))
                .multiply(new BigDecimal(org.getOrgAptitude().getWeight()));
        log.info("机构验证通过：{}-{}，用户:{}-{},第一次权重:{}",
                org.getOrgId(), org.getOrgName(),
                userAptitude.getName(), userAptitude.getMobile(),
                value);
        if (null != org.getWeight2() && org.getWeight2() > 0) {
            value = value.multiply(BigDecimal.valueOf(org.getWeight2()));
            log.info("机构验证通过：{}-{}，用户:{}-{},第二次权重:{}",
                    org.getOrgId(), org.getOrgName(),
                    userAptitude.getName(), userAptitude.getMobile(),
                    value);
        }
        // 用当前权重除以重复分发的次数得到一个新的权重 重复分发的次数越少 新权重越大
        // 获取当天重发次数
        Integer weight3 = dispatcheRecDao.countReDispatcheNum(org.getOrgId());
        log.info("orgId==" + org.getOrgId() + ";weight3==" + weight3);
        if (Objects.nonNull(weight3) && !weight3.equals(0)) {
            value = value.divide(BigDecimal.valueOf(weight3), 3, RoundingMode.HALF_UP);
            log.info("机构验证通过：{}-{}，用户:{}-{},第三次根据发送客户重复率计算权重:{}",
                    org.getOrgId(), org.getOrgName(),
                    userAptitude.getName(), userAptitude.getMobile(),
                    value);
        }
        org.setWeight(value.intValue());
        Double score;
        Integer highScoreCustomerCount = 0;
        Integer total = 0;
        if (null == org.getCityAptitudeRate()) {
            score = 0d;
        } else {
            highScoreCustomerCount = org.getCityAptitudeRate().getHighScoreCustomerCount();
            total = org.getDistributeCount();
            if (null == highScoreCustomerCount || highScoreCustomerCount <= 0 || (null == total || total <= 0)) {
                score = 0d;
            } else {
                score = new BigDecimal(highScoreCustomerCount).divide(new BigDecimal(total), 3, RoundingMode.HALF_UP).doubleValue();
            }
        }
        Double newScore = new BigDecimal(score).subtract(org.getOrgAptitude().getAmountRate()).setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
        log.info("机构验证通过：{}-{}，用户:{}-{},{},已方法数量：{}，高分客户数量：{}，高分客户占比：{}，计算后高分客户占比：{}，权重：{}",
                org.getOrgId(), org.getOrgName(),
                userAptitude.getName(), userAptitude.getMobile(),
                org.getKey(), total, highScoreCustomerCount, score, newScore, org.getWeight());
        org.setScore(newScore);
        newList.add(org);
    }

    private String getJudgeAptitudeForScore(UserAptitudePO po) {
        Double weight = po.getWeight();
        if (null == weight || weight < 0.05)
            return "ordinary";//一般客户
        if (weight.doubleValue() >= 0.05 && weight.doubleValue() < 0.093)
            return "well";//良好客户
        if (weight.doubleValue() >= 0.093 && weight.doubleValue() < 0.14)//优质客户
            return "excellent";
        if (weight.doubleValue() >= 0.14)//优秀客户
            return "importance";//重要客户
        return "ordinary"; // 无可贷点
    }

    public boolean isMainCity(UserAptitudePO userAptitude) {
        if (MainCity.CITY.contains(userAptitude.getCity())) {
            log.info("用户：{}-{}-{}，是主要城市", userAptitude.getName(), userAptitude.getMobile(), userAptitude.getCity());
            return true;
        }
        return false;
    }

    /**
     * 判断是否保留，true-保留，则不分发，false-不保留，分发
     *
     * @param newList      非专属公众号挑选出来的机构列表
     * @param userAptitude UserAptitudePO 用户属性对象
     * @return boolean  true-保留，false-不保留，分发出去
     */
    public boolean isRetain(List<OrgBO> newList, UserAptitudePO userAptitude) {
//        String[] cityArray = getOrgCityList();
        List<OrgBO> list = new ArrayList<>();
        list.addAll(newList);
        if (MainCity.CITY.contains(userAptitude.getCity()) && list.size() <= 2) {
            list.sort((e1, e2) -> e1.getOrgId().compareTo(e2.getOrgId()));
            if (list.size() == 2) {
                String orgIds = list.get(0).getOrgId() + "," + list.get(1).getOrgId();
                log.info("挑选非专属机构[3家机构]，是否保留该用户部分判断；挑选到的机构id列表—{}", orgIds);
                if ("11,12".equals(orgIds)) {
                    return true;
                } else return false;//不保留
            } else {
                for (OrgBO orgBO : list) {
                    log.info("挑选非专属机构，是否保留该用户部分判断[1家机构]；挑选到的机构id—{}", orgBO.getOrgId());
                    if (JudgeUtil.in(orgBO.getOrgId().intValue(), 11, 12)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public String[] getOrgCityList() {
        Object value = stringRedisTemplate.opsForValue().get("org_distribute_city_list");
        if (null == value) {
            String excludeIds = "11,12,113";//需要排除的机构id
            String week = DateUtil.getWeek(null);//当前星期几
            List<String> cityList = orgDao.getOrgCityList(week, excludeIds);
            if (CollectionUtil.isEmpty(cityList))
                return null;
            log.info("当前主要的城市列表：" + JSONUtil.toJsonString(cityList));
            stringRedisTemplate.opsForValue().set("org_distribute_city_list", JSONUtil.toJsonString(cityList), 10, TimeUnit.MINUTES);
            return cityList.toArray(new String[]{});
        }
        List<String> cityList = JSONUtil.toJavaBeanList(value.toString(), String.class);
        log.info("当前主要的城市列表[缓存]：" + JSONUtil.toJsonString(cityList));
        return cityList.toArray(new String[]{});
    }


    /**
     * 选择公众号专属机构
     *
     * @param orgBOList    List<OrgBO>
     * @param userAptitude UserAptitudePO
     * @return OrgBO
     */
    private Long chooseExclusiveOrg(List<OrgBO> orgBOList, UserAptitudePO userAptitude, UserDTO select) {
        if (orgBOList.isEmpty()) {
            log.info("{}-{}选择专属公众号机构：专属公众号机构列表是空的", userAptitude.getName(), userAptitude.getMobile());
            return null;
        }
        String userChannel = userAptitude.getChannel();
        if (StringUtils.isBlank(userChannel)) {
            log.info("{}-{},选择专属公众号机构：用户渠道是空的", userAptitude.getName(), userAptitude.getMobile());
            return null;
        }
        List<OrgBO> list = new ArrayList<>();
        for (int i = 0; i < orgBOList.size(); i++) {
            OrgBO orgBO = orgBOList.get(i);
            OrgAptitudePO orgAptitude = orgBO.getOrgAptitude();
            if (StringUtils.isBlank(orgAptitude.getChannel())) {
                continue;
            }
            orgBO.checkCity(userAptitude)
                    .checkDistributeTime(userAptitude)
                    .checkDistributeCount(orgDao);
            if (!orgBO.getCheckPass())
                continue;
            if (JudgeUtil.in(userChannel, orgAptitude.getChannel().split(","))) {
                list.add(orgBO);
            }
        }
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        list.sort((e1, e2) -> -e1.getDistributeCount().compareTo(e2.getDistributeCount()));
        for (OrgBO org : list) {
            userAptitude.setOrgId(org.getOrgId());
            log.info("用户号码:{},公众号:{},挑选到专属机构：{}-{}", userAptitude.getMobile(), userAptitude.getWxType(), org.getOrgId(), org.getOrgName());
            ApiSender sender = AppContextUtil.getBean("apiSender_" + org.getOrgId(), ApiSender.class);
            SendResult send = sender.send(userAptitude, select);
            log.info("专属机构:{}-{},用户号码:{},发送结果:{}-{}", org.getOrgId(), org.getOrgName(), userAptitude.getMobile(), send.isSuccess(), send.getResultMsg());
            if (send.isSuccess()) {
                return org.getId();
            }
        }
        return null;
    }


    @Override
    public void updateOrgDistributeCount(OrgBO orgBO, UserAptitudePO po, Boolean isExclusive) {
        if (StringUtils.isNotBlank(orgBO.getOrgAptitude().getWechat()) || isExclusive) {
            return;
        }
        List<OrgBO> list = this.getCacheAll(isExclusive);
        boolean fresh = false;
        for (OrgBO org : list) {
            if (StringUtils.isBlank(orgBO.getOrgAptitude().getCity())) {
                if (orgBO.getOrgId().longValue() == org.getOrgId().longValue()) {
                    org.setDistributeCount(org.getDistributeCount() + 1);
                    fresh = true;
                    break;
                }
            } else {
                if (orgBO.getOrgId().longValue() == org.getOrgId().longValue() && orgBO.getOrgAptitude().getCity().equals(po.getCity())) {
                    org.setDistributeCount(org.getDistributeCount() + 1);
                    fresh = true;
                    break;
                }
            }
        }
        log.info("机构:{}-{}-{},用户:{}-{},更新缓存:{}", orgBO.getOrgId(), orgBO.getOrgName(), orgBO.getOrgAptitude().getCity(), po.getMobile(), po.getCity(), fresh);
        if (fresh) {
            stringRedisTemplate.opsForValue().set(DISTRIBUTE_ORG_LIST_KEY, JSONUtil.toJsonString(list));// 更新缓存
        }

    }

    @Override
    public List<OrgDistributeStatisticsBO> getOrgDistributeStatistics(String startDate, String endDate) {
        return orgDao.selectOrgDistributeCount(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> getOrgDistributeCountSummation(String startDate, String endDate) {
        return orgDao.selectOrgDistributeCountSummation(startDate, endDate);
    }

    @Override
    public Integer getDistributeSuccessNotNameCounts(String startDate, String endDate, Long orgId) {
        Integer count = orgDao.selectDistributeSuccessNotNameCounts(startDate, endDate, orgId);
        if (null == count)
            return 0;
        return count;
    }

    @Override
    public List<Map<String, Object>> getChannelQualityForAverage(String startDate, String endDate) {
        return orgDao.selectChannelQualityForAverage(startDate, endDate, MainCity.CITY);
    }

    @Override
    public List<Map<String, Object>> getChannelQuality(String startDate, String endDate) {
        return orgDao.selectChannelQuality(startDate, endDate, MainCity.CITY);
    }


    @Override
    public List<Map<String, Object>> getlineLenderCountSummation(String startDate, String endDate) {
        return orgDao.selectonlineLenderCountSummation(startDate, endDate);
    }


    @Override
    public List<UserAptitudePO> getOrgDistributeSuccessUser(String startDate, String endDate, String... orgIdArray) {
        if (null == orgIdArray || orgIdArray.length == 0
                || StringUtils.isBlank(startDate)
                || StringUtils.isBlank(endDate)) {
            return null;
        }

        StringBuffer orgIds = new StringBuffer();
        for (int i = 0; i < orgIdArray.length; i++) {
            if (i == 0) {
                orgIds.append(orgIdArray[i]);
            } else orgIds.append(",").append(orgIdArray[i]);
        }
        return orgDao.selectOrgDistributeSuccessUser(startDate, endDate, orgIds.toString());
    }

    @Override
    public List<Map<String, String>> getWechatChannelConversion(String startDate, String endDate) {
        return null;
    }


}
