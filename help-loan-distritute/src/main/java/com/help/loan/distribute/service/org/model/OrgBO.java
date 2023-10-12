package com.help.loan.distribute.service.org.model;

import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.cityAptitude.dao.CityAptitudeRatePOMapper;
import com.help.loan.distribute.service.cityAptitude.model.CityAptitudeRatePO;
import com.help.loan.distribute.service.org.dao.OrgDao;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.util.DisConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class OrgBO extends OrgPO {
    private static final Logger log = LoggerFactory.getLogger(OrgBO.class);
    private OrgAptitudePO orgAptitude; // 机构分城市配置属性
    private String distributeDate; // 分发日期
    private Double score;//待分发的用户分数值
    private Integer weight;//权重值
    private Integer weight2;//权重值
    private Integer distributeCount = Integer.valueOf(0);
    private Long distributeSort; // 排序值
    private Boolean checkPass = Boolean.valueOf(true);
    private BigDecimal houseAndPublicFundRate = new BigDecimal(100);//有房和公积金比率
    private boolean exceedNoAptitudeRate=false; // 超出没有可贷点资质
    private boolean exceedAptitudeRate = false; // 超出资质
    private CityAptitudeRatePO cityAptitudeRate;// 一定日期内机构城市分发成功的各项资质占比兑现
    private Integer distributeRepeatCount;

    private String key; // 机构城市资质占比的key值

    public Integer getWeight2() {
        return weight2;
    }

    public void setWeight2(Integer weight2) {
        this.weight2 = weight2;
    }

    public String getKey() {
        return key;
    }
    public Double getScore() {
        return score;
    }
    public void setScore(Double score) {
        this.score = score;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public CityAptitudeRatePO getCityAptitudeRate() {
        return cityAptitudeRate;
    }
    public void setCityAptitudeRate(CityAptitudeRatePO cityAptitudeRate) {
        this.cityAptitudeRate = cityAptitudeRate;
    }
    public boolean isExceedAptitudeRate() {
        return exceedAptitudeRate;
    }
    public void setExceedAptitudeRate(boolean exceedAptitudeRate) {
        this.exceedAptitudeRate = exceedAptitudeRate;
    }
    public BigDecimal getHouseAndPublicFundRate() {
        return houseAndPublicFundRate;
    }
    public void setHouseAndPublicFundRate(BigDecimal houseAndPublicFundRate) {
        this.houseAndPublicFundRate = houseAndPublicFundRate;
    }
    public Integer getWeight() {
        return weight;
    }
    public void setWeight(Integer weight) {
        this.weight = weight;
    }
    public void setDistributeSort(Long distributeSort) {
        this.distributeSort = distributeSort;
    }

    public void setDistributeCount(Integer distributeCount) {
        this.distributeCount = distributeCount;
    }

    public Integer getDistributeRepeatCount() {
        return distributeRepeatCount;
    }

    public void setDistributeRepeatCount(Integer distributeRepeatCount) {
        this.distributeRepeatCount = distributeRepeatCount;
    }

    public void setDistributeDate(String distributeDate) {
        this.distributeDate = distributeDate;
    }

    public void setOrgAptitude(OrgAptitudePO orgAptitude) {
        this.orgAptitude = orgAptitude;
    }

    public boolean getExceedNoAptitudeRate() {
        return exceedNoAptitudeRate;
    }
    public void setExceedNoAptitudeRate(boolean exceedNoAptitudeRate) {
        this.exceedNoAptitudeRate = exceedNoAptitudeRate;
    }
    public void setCheckPass(Boolean checkPass) {
        this.checkPass = checkPass;
    }
    public OrgAptitudePO getOrgAptitude() {
        return this.orgAptitude;
    }

    public String getDistributeDate() {
        return this.distributeDate;
    }

    public Integer getDistributeCount() {
        return this.distributeCount;
    }

    public Long getDistributeSort() {
        return this.distributeSort;
    }

    public Boolean getCheckPass() {
        return this.checkPass;
    }

    /**
     * 验证是否符合城市
     *
     * @param po UserAptitudePO
     * @return 当前OrgBO对象
     */
    public OrgBO checkCity(UserAptitudePO po) {
        if(!getCheckPass().booleanValue()) {
            return this;
        }
        String orgCity = this.orgAptitude.getCity();
        if(StringUtils.isBlank(orgCity)) {// 如果机构没有配置限制城市，则表示允许所有城市
            return this;
        }
        if("全国".equals(orgCity)){
            return this;
        }
        if(StringUtils.isBlank(po.getCity())) {
            log.info("机构:{}-{}-{},用户:{}[用户城市是空的不符]",getOrgId(), getOrgNickname(),orgCity, po.getMobile());
            setCheckPass(Boolean.valueOf(false));
            return this;
        }
        String userCity = po.getCity().endsWith("市")?po.getCity():po.getCity() + "市";
        String[] cityArray = orgCity.split(",");
        if(JudgeUtil.in(userCity,cityArray)){
            this.setWeight2(10);
            log.info("机构:{}-{}-,用户:{}-{},[城市符合]", getOrgId(), getOrgName(),orgCity, po.getMobile(), userCity);
            return this;
        }
        if(orgCity.contains("全国")){
            log.info("机构:{}-{}-,用户:{}-{},[城市符合]", getOrgId(), getOrgName(),orgCity, po.getMobile(), userCity);
            return this;
        }
        log.info("机构:{}-{}-{},用户:{}-{}[城市不符合]", getOrgId(), getOrgName(),orgCity, po.getMobile(), userCity);
        setCheckPass(Boolean.valueOf(false));
        return this;
    }

    /**
     * 验证是否在分发时间段内，如果机构列表缓存1小时，可以考虑在查询机构列表时验证，以减少该段代码的执行次数
     *
     * @param po UserAptitudePO
     * @return 当前OrgBO
     */
    public OrgBO checkDistributeTime(UserAptitudePO po) {
        if(!getCheckPass().booleanValue()) {
            return this;
        }
        String week = DateUtil.getWeek(null);
        String orgWeek = getOrgAptitude().getWeek();
        if((StringUtils.isBlank(orgWeek)) || !orgWeek.contains(week)) {
            log.debug("机构:{}-{},城市:{},分发日期:{},今天日期:{}-[日期不符]", new Object[]{getOrgId(), getOrgName(), getOrgAptitude().getCity(), getOrgAptitude().getWeek(), week});
            setCheckPass(Boolean.valueOf(false));
            return this;
        }
        String limitTime = getOrgAptitude().getLimitTime();
        if(StringUtils.isBlank(limitTime)) {
            return this;
        }
        String[] array = limitTime.split("-");
        if(array.length != 2) {
            setCheckPass(Boolean.valueOf(false));
            log.debug("机构机构:{}-{},城市:{},时间配置:{}[时间段配置错误]", new Object[]{getOrgId(), getOrgName(), getOrgAptitude().getCity(), limitTime});
            return this;
        }
        int start = Integer.valueOf(array[0]).intValue();
        int end = Integer.valueOf(array[1]).intValue() - 1;
        int distributeTime = Integer.valueOf(DateUtil.formatToString(new Date(), "HH")).intValue();
        if((distributeTime >= start) && (distributeTime <= end)) {
            this.setCheckPass(true);
            return this;
        }
        setCheckPass(Boolean.valueOf(false));
        log.debug("机构:{}-{},城市:{},时间段:{},现在:{}-[时间段不匹配]", new Object[]{getOrgId(), getOrgName(), getOrgAptitude().getCity(), limitTime, Integer.valueOf(distributeTime)});
        return this;
    }

    /**
     * 验证分发数量是否超量，跟获取机构列表配合（获取机构列表时也会验证是否超量，如果超量则从列表中剔除，减少验证次数）
     *
     * @param orgDao OrgDao机构数据库映射对象
     * @return 当前OrgBO对象
     */
    public OrgBO checkDistributeCount(OrgDao orgDao) {
        if(!getCheckPass().booleanValue()) {
            return this;
        }
        String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        String city = this.getOrgAptitude().getCity();
        if(StringUtils.isNotBlank(city) && city.contains("全国")){
            city = null;
        }
        Integer cityCount = orgDao.selectTodayDistributeSuccessCount2(this.getOrgId(), city, startDate, endDate);
        this.setDistributeCount((null == cityCount ? 0: cityCount));
        if(null == cityCount || cityCount.intValue() == 0) {
            this.setCheckPass(true);
            return this;
        }
        if(this.getDistributeCount().intValue() >= getOrgAptitude().getLimitCount().intValue()) {
            log.debug("机构:{}-{},城市:{},限制数量:{},已分发数量:{}[已到量]", getOrgId(), getOrgName(), getOrgAptitude().getCity(), getOrgAptitude().getLimitCount(), getDistributeCount());
            setCheckPass(Boolean.valueOf(false));
            return this;
        }
        return this;
    }

    /**
     * 验证用户的贷款金额是否超过限制比例
     *
     * @param po      UserAptitudePO
     * @param userDao UserAptitudeDao
     * @return 当前:OrgBO对象
     */
    public OrgBO checkLoanAmount(UserAptitudePO po, UserAptitudeDao userDao) {
        if(!getCheckPass()) {
            return this;
        }
        if(StringUtils.isBlank(this.getOrgAptitude().getLoanAmount())) {
            log.debug("机构:{}-{}[贷款金额限制比例字段未配置]", this.getOrgId(), this.getOrgName());
            this.setCheckPass(true);
            return this;
        }
        try {
            // 大概格式：{"《3-5万》":0.6,"《5-10万》":0.2,"《10-30万》":0.1,"《30-50万》":0.1}
            JSONObject loanAmountRate = JSONUtil.toJSON(this.getOrgAptitude().getLoanAmount());
            log.debug("机构:{}-{},限制比例:{}", this.getOrgId(), this.getOrgName(), this.getOrgAptitude().getLoanAmount());
            Iterator<String> it = loanAmountRate.keySet().iterator();
            boolean pass = true;
            while(it.hasNext()) {
                String key = it.next();
                // 如果用户的金额和设定的金额字符不同，则不需要验证
                if(StringUtils.isBlank(key) || !key.equals(po.getLoanAmount())) {
                    continue;
                }
                String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
                String startDate = date + " 00:00:00";
                String endDate = date + " 23:59:59";
                Integer counts = userDao.todayOrgLoanAmountCount(this.getOrgId(), startDate, endDate, key);
                if(null == counts || counts.intValue() <= 0) {
                    continue;
                }
                double rate = new BigDecimal(counts).divide(new BigDecimal(getOrgAptitude().getLimitCount()), 2, 4).doubleValue();
                double value = loanAmountRate.getDoubleValue(key);
                if(rate >= value) {
                    log.debug("机构:{}-{},金额:{},限制比例:{},当前比例:{}[超出限制比例]", this.getOrgId(), this.getOrgName(), key, value, rate);
                    pass = false;
                    break;
                }
            }
            this.setCheckPass(pass);
            return this;
        } catch(Exception e) {
            log.error("验证贷款金额限制比例异常", e);
            this.setCheckPass(true);
            return this;
        }
    }

    /**
     * 验证指定贷款金额的用户占比，如果超过配置比例，则验证不通过，否则验证通过
     *
     * @param po            UserAptitudePO用户属性对象
     * @param userDao       用户数据库映射对象
     * @param loanAmountStr 验证的金额字符范围，例如：《3-5万》
     * @return 返回当前OrgBO对象
     */
    public OrgBO checkLoanAmount(UserAptitudePO po, UserAptitudeDao userDao, String loanAmountStr) {
        if(!getCheckPass().booleanValue()) {
            return this;
        }
        if((null == userDao) || (null == po)) {
            log.debug("机构:{}-{},城市:{}[UserAptitudePO对象或者UserAptitudeDao对象为空]", new Object[]{getOrgId(), getOrgName(), getOrgAptitude().getCity()});
            setCheckPass(Boolean.valueOf(false));
            return this;
        }
        // 如果用户金额字符和限定字符不同，则不需要验证
        if(!loanAmountStr.equals(po.getLoanAmount())) {
            this.setCheckPass(true);
            return this;
        }
        String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        Integer counts = userDao.todayOrgCityLoanAmountCount(getOrgId(), po.getCity(), startDate, endDate, loanAmountStr);
        if((null == counts) || (counts.intValue() <= 0) || (getDistributeCount().intValue() <= 0)) {
            setCheckPass(true);
            return this;
        }
        BigDecimal rate = new BigDecimal(counts.intValue()).divide(new BigDecimal(getOrgAptitude().getLimitCount().intValue()), 2, 4);
        log.debug("机构:{}-{},城市:{}-{}限制比率:{}", this.getOrgId(), this.getOrgName(), po.getCity(), rate.doubleValue(), this.getOrgAptitude().getAmountRate().doubleValue());
        if(rate.compareTo(getOrgAptitude().getAmountRate()) >= 0) {
            log.info("机构:{}-{},城市:{},贷款金额限制比:{},当前占比:{}[已达到贷款金额限制比率]", new Object[]{getOrgId(), getOrgName(), getOrgAptitude().getCity(), Double.valueOf(getOrgAptitude().getAmountRate().doubleValue()), Double.valueOf(rate.doubleValue())});
            setCheckPass(false);
            return this;
        }
        return this;
    }

    public OrgBO checkName(UserAptitudePO po, UserAptitudeDao userDao) {
        if(!this.getCheckPass()) {
            return this;
        }
        if(StringUtils.isNotBlank(po.getName())) {
            this.setCheckPass(true);
            return this;
        }
        String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        Integer counts = userDao.todayOrgEmptyNameCount(this.getOrgId(), startDate, endDate);
        double rate = new BigDecimal(counts).divide(new BigDecimal(getOrgAptitude().getLimitCount()), 2, 4).doubleValue();
        double limitRate = 0.15d;
        if(JudgeUtil.in(this.getOrgId().toString(),"11","12")){
            limitRate = 0.6d;
        }
        if(rate >= limitRate) {
            log.debug("机构:{}-{},没有姓名字段的用户比例：｛｝[超出15%的比例]", this.getOrgId(), this.getOrgName(), rate);
            this.setCheckPass(false);
            return this;
        }
        return this;
    }

    /**
     * 验证当钱机构城市分发成功无资质占比超过整体无资质占比，则分发给他，返回false
     * @param cityAptitudeRatePOMapper
     * @param po UserAptitudePO
     * @return OrgBO
     */
    public OrgBO checkOrgCityNoAptitudeRate(CityAptitudeRatePOMapper cityAptitudeRatePOMapper,UserAptitudePO po){
        if(!this.getCheckPass())
            return this;
        if(JudgeUtil.in(this.getOrgId().intValue(),11,12))
            return this;
        if(!isNoAptitude(po)){
            log.info("验证机构城市分发成功无资质占比：机构-{}-{}-{}【当前用户有可贷点】",this.getOrgId(),getOrgName(),po.getMobile());
            return this;
        }
        String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        Map<String,Object> map = cityAptitudeRatePOMapper.selectOrgCityNoAptitudeRate(this.getOrgId(),po.getCity(),startDate,endDate);
        if(null == map)
            return this;
        Object orgCityNoAptitudeRate = map.get("noAptitudeRate");
        if(null == orgCityNoAptitudeRate || Double.valueOf(orgCityNoAptitudeRate.toString()).doubleValue() <= 18d){
            return this;
        }
        CityAptitudeRatePO cityAptitudeRatePO = cityAptitudeRatePOMapper.selectCityAptitudeRatePO(date,po.getCity());
        if(null == cityAptitudeRatePO || Double.valueOf(orgCityNoAptitudeRate.toString()).compareTo(cityAptitudeRatePO.getNoAptitude()) <= 0){
            return  this;
        }
        log.debug("验证机构城市分发成功无资质占比：机构-{}-{},当前无可贷点占比：{}%，{}-整体无可贷占比：{}%【超出当前城市无可贷点占比】",this.getOrgId(),getOrgName(),orgCityNoAptitudeRate.toString(),po.getCity(),cityAptitudeRatePO.getNoAptitude());
        this.setExceedNoAptitudeRate(true);
        return this;
    }

    /**
     * 验证当前机构城市已分发用户资质占比是否超过整体占比
     * @param cityAptitudeRatePOMapper  CityAptitudeRatePOMapper
     * @param po UserAptitudePO
     * @return OrgBO
     */
    public OrgBO checkOrgCityAptitudeRate(CityAptitudeRatePOMapper cityAptitudeRatePOMapper,UserAptitudePO po){
        if(!this.getCheckPass())
            return this;
        if(null == getCityAptitudeRate())
            return this;
        String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        //当前城市获客各项资质占比
        CityAptitudeRatePO currentCityAptitudeRate = cityAptitudeRatePOMapper.selectCityAptitudeRatePO(date,this.getOrgAptitude().getCity());
        if(null == currentCityAptitudeRate) {
            log.debug("机构-{}-{}-{}，用户：{}-{}，无整体资质占比",getOrgId(),getOrgName(),getOrgAptitude().getCity(),po.getMobile(),po.getCity());
            return this;
        }
        String key = getJudgeAptitude(po);
        this.setKey(key);
        if(StringUtils.isBlank(key)){
            if(this.getCityAptitudeRate().getNoAptitude().compareTo(currentCityAptitudeRate.getNoAptitude()) >= 0){
                log.debug("机构-{}-{}-{}，用户：{}-{}，机构【无可贷点】超过整体资质占比,key:{}",getOrgId(),getOrgName(),getOrgAptitude().getCity(),po.getMobile(),po.getCity(),key);
                this.setExceedNoAptitudeRate(true);
                return this;
            }else{
                log.debug("机构-{}-{}-{}，用户：{}-{}，机构【无可贷点】-没有-超过整体资质占比,key:{}",getOrgId(),getOrgName(),getOrgAptitude().getCity(),po.getMobile(),po.getCity(),key);
                return this;
            }
        }
        JSONObject orgAptitudeRate = JSONUtil.toJSON(this.getCityAptitudeRate());
        JSONObject integralAptitudeRate = JSONUtil.toJSON(currentCityAptitudeRate);
        if (orgAptitudeRate.containsKey(key) && integralAptitudeRate.containsKey(key)) {
            Double orgRate = orgAptitudeRate.getDouble(key);
            Double integralRate = integralAptitudeRate.getDouble(key);
            if(orgRate.compareTo(integralRate) >= 0){
                log.debug("机构-{}-{}-{}，用户：{}-{}，机构【{}】超过整体资质占比:{}-{}",getOrgId(),getOrgName(),getOrgAptitude().getCity(),po.getMobile(),po.getCity(),key,orgRate,integralRate);
                this.setExceedAptitudeRate(true);
                return this;
            }
            log.debug("机构-{}-{}-{}，用户：{}-{}，机构【{}】-没有-超过整体资质占比:{}-{}",getOrgId(),getOrgName(),getOrgAptitude().getCity(),po.getMobile(),po.getCity(),key,orgRate,integralRate);
            return this;
        }
        this.setCheckPass(true);
        log.debug("机构-{}-{}-{}，用户：{}-{},key-{}【未超过整体资质】",getOrgId(),getOrgName(),getOrgAptitude().getCity(),po.getMobile(),po.getCity(),key);
        return this;
    }

    public OrgBO checkScoreRate(CityAptitudeRatePOMapper cityAptitudeRatePOMapper,UserAptitudePO po){
        if(!this.getCheckPass()){
            return this;
        }
        String key = judgeUserScore(po);
        this.setKey(key);
        return this;
    }

    public OrgBO checkChannel2(UserAptitudePO po){
        if(!this.getCheckPass())
            return this;
        if(null == this || null == po){
            return this;
        }
        OrgAptitudePO orgAptitude = this.getOrgAptitude();
        if(null == orgAptitude || StringUtils.isBlank(orgAptitude.getChannel())){
            return this;
        }
        if(!JudgeUtil.contain(po.getChannel(),orgAptitude.getChannel())){
            this.setCheckPass(false);
        }
        return this;
    }

    /**
     * 验证贷款类型,如果是信贷,直接过,如果不是信贷则验证机构接收房抵或者车抵
     * @param po
     * @return
     */
    public OrgBO checkCreditType(UserAptitudePO po){
        if(!this.getCheckPass())
            return this;
        if(JudgeUtil.in(po.getType(),this.getOrgAptitude().getType())){
            this.setCheckPass(true);
            return this;
        }
        log.info("机构:{}-{},产品类型验证:房抵or车抵or信用卡逾期:{}-{}-{}[产品类型不符合]",this.getOrgNickname(),this.getOrgAptitude().getType(),po.getName(),po.getCreateBy(),po.getType());
        this.setCheckPass(false);
        return this;
    }

    // 验证机构重复值的阈值
    public OrgBO checkRepeatCount(CityAptitudeRatePOMapper cityAptitudeRatePOMapper,UserAptitudePO po){
        if(!this.getCheckPass()){
            return this;
        }
        // 如果机构没有分发重复 或者 机构重复数量为0  则通过
        if(null == this.getOrgAptitude().getRepeatRate() || 0 == this.getOrgAptitude().getRepeatRate().intValue()){
            this.setCheckPass(true);
            return this;
        }
        // 如果机构城市没有设置分发重复阈值  则通过
        if(null == this.getOrgAptitude().getRepeatRate() || 0 == this.getOrgAptitude().getRepeatRate().intValue()){
            this.setCheckPass(true);
            return this;
        }
        //如果机构分发重复数量 大于机构设置的阈值 则不通过
        if(this.getDistributeRepeatCount().intValue() > this.getOrgAptitude().getRepeatRate().intValue()){
            log.info("{}-{}，{}：{} >= {}【机构城市重复客户超阈值】", this.getOrgName(),po.getCity(),key,this.getDistributeRepeatCount(),this.getOrgAptitude().getRepeatRate());
            this.setCheckPass(false);
            return this;
        }
        this.setCheckPass(true);
        return this;
    }


    public OrgBO checkChannel(UserAptitudePO po){
        if(!this.getCheckPass()){
            return this;
        }
        String orgChannel  = this.getOrgAptitude().getChannel();
        if(StringUtils.isBlank(orgChannel)){
            this.setCheckPass(false);
            return this;
        }
        String[] channels  = orgChannel.split(",");
        if(!JudgeUtil.in(po.getChannel(),channels)){
            this.setCheckPass(false);
            return this;
        }
        this.setCheckPass(true);
        return this;
    }

    private String judgeUserScore(UserAptitudePO po){
        Double weight = po.getWeight();
        if(null == weight || weight < 0.05)
            return "ordinary";//一般客户
        if(weight.doubleValue() >= 0.05 && weight.doubleValue() < 0.093)
            return "well";//良好客户
        if(weight.doubleValue() >=  0.093 && weight.doubleValue() < 0.14 )
            return "excellent";
        if(weight.doubleValue() > 0.14)//优秀客户
            return "importance";//重要客户
        return "ordinary"; // 无可贷点
    }

    private String getJudgeAptitude(UserAptitudePO po){
        if(po.getPublicFund().contains("有，") && JudgeUtil.in(po.getGetwayIncome(),1,2)){
            return "publicFundIncome";
        }
        if(po.getPublicFund().contains("有，") ){
            return "publicFund";
        }
        if(JudgeUtil.in(po.getHouse(),1,2) ){
            return "house";
        }
        if(JudgeUtil.in(po.getInsurance(),1,2)){
            return "insurance";
        }
        if(JudgeUtil.in(po.getGetwayIncome(),1,2)){
            return "income";
        }
        if(JudgeUtil.in(po.getCompany(),1)){
            return "company";
        }
        if(JudgeUtil.in(po.getCar(),1,2)){
            return "car";
        }
        return "noAptitude"; // 无可贷点
    }


    private boolean isNoAptitude(UserAptitudePO po){
        if(null == po)
            return true;

        if(JudgeUtil.in(po.getHouse(),1,2) || JudgeUtil.in(po.getCar(),1,2)
                    || po.getPublicFund().contains("有，") || JudgeUtil.in(po.getGetwayIncome(),1,2)){

            return false;
        }
        return true;
    }

    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
