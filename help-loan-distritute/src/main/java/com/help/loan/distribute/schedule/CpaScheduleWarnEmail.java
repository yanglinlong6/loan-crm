package com.help.loan.distribute.schedule;

import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.email.EmailBO;
import com.help.loan.distribute.common.email.EmailService;
import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.schedule.model.CitySummarizingBO;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.org.OrgService;
import com.help.loan.distribute.service.org.model.OrgDistributeStatisticsBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.*;

@Component
public class CpaScheduleWarnEmail {

    @Autowired
    private OrgService orgService;

    private static final Logger log = LoggerFactory.getLogger(CpaScheduleWarnEmail.class);

    private static final String emailAddress = "wangping@bangzheng100.com,panlikun@bangzheng100.com,wanglijing@bangzheng100.com,814481025@qq.com";

    @Scheduled(cron = "0 0/20 7-23 * * ?")
    public void sendWarnEmail() {
        try {
            log.info("机构分发统计提醒邮件开始：{}", DateUtil.formatToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
            String startDate = date + " 00:00:00";
            String endDate = date + " 23:59:59";
            StringBuffer content = new StringBuffer();

//            content.append(jointOnlineLenderSummarizing(startDate,endDate));
            //线下机构分发数据
            List<Map<String,Object>> offlineOrgSummations = orgService.getOrgDistributeCountSummation(startDate,endDate);
            content.append(joiontChannelQuality(startDate,endDate,offlineOrgSummations));
            content.append(jointOfflineOrgSummarizing(startDate,endDate,offlineOrgSummations));
//            content.append(jointOfflineOrgSummarizingDifferentiateCity(startDate,endDate));
            EmailBO email = new EmailBO(date + "邦正提醒邮件", content.toString(), emailAddress);
            EmailService.sendMessage(email);
            log.info("机构分发统计提醒邮件成功：{}", DateUtil.formatToString(new Date(), DateUtil.yyyyMMddHHmmss2));
        } catch(MessagingException e) {
            log.error("每日分发邮件发送异常：{}", e.getMessage(), e);
        }
    }

    private String joiontChannelQuality(String startDate,String endDate,List<Map<String,Object>> offlineOrgSummations){
        StringBuffer content = new StringBuffer();
        try {
            content.append("----------------------------------当天各城市需求量汇总----------------------------------")
                    .append(jointTodayCityNeedCount(offlineOrgSummations)).append("</br>");

            content.append("----------------------------------当天渠道高分客户占比详情----------------------------------开始").append("</br>");
            List<Map<String,Object>> averageData = orgService.getChannelQualityForAverage(startDate,endDate);
            if(!CollectionUtil.isEmpty(averageData)){
                content.append("<table border='1' cellspacing='0'>");
                content.append("<tr>")
                        .append("<td>").append("城市").append("</td>")
                        .append("<td>").append("当日获客数量").append("</td>")
                        .append("<td>").append("一般客户").append("</td>")
                        .append("<td>").append("良好客户").append("</td>")
                        .append("<td>").append("优秀客户").append("</td>")
                        .append("<td>").append("重要客户").append("</td>")
                        .append("<td>").append("一般占比").append("</td>")
                        .append("<td>").append("良好占比").append("</td>")
                        .append("<td>").append("优秀占比").append("</td>")
                        .append("<td>").append("重要占比").append("</td>")
                        .append("</tr>");
                for (Map<String,Object> map : averageData){
                    content.append("<tr>")
                            .append("<td>").append(map.get("城市")).append("</td>")
                            .append("<td>").append(map.get("当日获客数量")).append("</td>")
                            .append("<td>").append(map.get("一般客户")).append("</td>")
                            .append("<td>").append(map.get("良好客户")).append("</td>")
                            .append("<td>").append(map.get("优秀客户")).append("</td>")
                            .append("<td>").append(map.get("重要客户")).append("</td>")
                            .append("<td>").append(map.get("一般占比")).append("</td>")
                            .append("<td>").append(map.get("良好占比")).append("</td>")
                            .append("<td>").append(map.get("优秀占比")).append("</td>")
                            .append("<td>").append(map.get("重要占比")).append("</td>")
                            .append("</tr>");;
                }
                content.append("</table>").append("</br>");
            }
            List<Map<String,Object>> datas = orgService.getChannelQuality(startDate,endDate);
            if(!CollectionUtil.isEmpty(datas)){
                content.append("<table border='1' cellspacing='0'>");
                content.append("<tr>")
                        .append("<td>").append("城市").append("</td>")
                        .append("<td>").append("渠道").append("</td>")
                        .append("<td>").append("当日获客数量").append("</td>")
                        .append("<td>").append("高分数量").append("</td>")
                        .append("<td>").append("连接失败数量").append("</td>")
                        .append("<td>").append("高分客户占比").append("</td>")
                        .append("</tr>");

                for (Map<String,Object> map : datas){
                    content.append("<tr>")
                            .append("<td>").append(map.get("城市")).append("</td>")
                            .append("<td>").append(map.get("渠道")).append("</td>")
                            .append("<td>").append(map.get("当日获客数量")).append("</td>")
                            .append("<td>").append(map.get("高分数量")).append("</td>")
                            .append("<td>").append(map.get("连接失败数量")).append("</td>")
                            .append("<td>").append(map.get("高分客户占比")).append("</td>")
                            .append("</tr>");;
                }
                content.append("</table>").append("</br>");
            }
            content.append("----------------------------------当天渠道高分客户占比详情----------------------------------结束").append("</br>");
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return content.toString();
    }

    /**
     * 汇总当日各城市需求量
     * @param offlineOrgSummations 线下机构城市汇总数据
     * @return html邮件数据
     */
    private String jointTodayCityNeedCount(List<Map<String,Object>> offlineOrgSummations){
        if(CollectionUtil.isEmpty(offlineOrgSummations))
            return null;
        StringBuffer content = new StringBuffer();
        Map<String, CitySummarizingBO> cityMap = new HashMap<>();
        for(Map<String,Object> summation : offlineOrgSummations) {
            if(null == summation)
                continue;
            int limitCount = Integer.valueOf(summation.get("每日限量") == null ? "0":summation.get("每日限量").toString());
            int successCount = Integer.valueOf((summation.get("分发成功数") == null? "0":summation.get("分发成功数").toString()));
            if(limitCount <= 0 && successCount <=0 )
                continue;
            String city = summation.get("机构城市") == null ? "不限制":summation.get("机构城市").toString();
            BigDecimal orgIncome = new BigDecimal(summation.get("预期收益")== null?"0":summation.get("预期收益").toString());

            // 默认产品类型是: 综合信贷
            Byte orgAptitudeType = DistributeConstant.LoanType.CREDIT;
            Object type = summation.get("type");
            log.info("产品类型:{}",type);
            if(null != type)
                orgAptitudeType = Byte.valueOf(type.toString());
            String productType = getLoanProductType(orgAptitudeType);
            city = city + "-"+productType;
            if(cityMap.containsKey(city)){
                CitySummarizingBO citySummarizing = cityMap.get(city);
                citySummarizing.setLimitCouint(citySummarizing.getLimitCouint()+limitCount);
                citySummarizing.setIncome(citySummarizing.getIncome()+orgIncome.doubleValue());
                citySummarizing.setSendSuccessCount(citySummarizing.getSendSuccessCount()+successCount);
                cityMap.put(city,citySummarizing);
            }else{
                CitySummarizingBO citySummarizing = new CitySummarizingBO();
                citySummarizing.setCity(city);
                citySummarizing.setSendSuccessCount(successCount);
                citySummarizing.setLimitCouint(limitCount);
                citySummarizing.setIncome(orgIncome.doubleValue());
                cityMap.put(city,citySummarizing);
            }
        }

        if(CollectionUtil.isEmpty(cityMap))
            return null;
        content.append("<table border='1' cellspacing='0'>");
        content.append("<tr>")
                .append("<td>").append("城市").append("</td>")
                .append("<td>").append("总限量").append("</td>")
                .append("<td>").append("已分发成功数").append("</td>")
                .append("<td>").append("城市预期收益").append("</td>")
                .append("</tr>");
        Iterator<Map.Entry<String,CitySummarizingBO>> iterator = cityMap.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,CitySummarizingBO> entry = iterator.next();
            CitySummarizingBO citySummarizing  = entry.getValue();
            if(citySummarizing.getCity().equals("不限制") || citySummarizing.getCity().equals("18个城市"))
                continue;
            content.append("<tr>")
                    .append("<td>").append(citySummarizing.getCity()).append("</td>")
                    .append("<td>").append(citySummarizing.getLimitCouint()).append("</td>")
                    .append("<td>").append(citySummarizing.getSendSuccessCount()).append("</td>")
                    .append("<td>").append(citySummarizing.getIncome()).append("</td>")
                    .append("</tr>");
        }
        content.append("</table>").append("</br>");
        return  content.toString();
    }


    private String getLoanProductType(Byte type){
        if(JudgeUtil.in(type, DistributeConstant.LoanType.CREDIT))
            return "信贷";
        if(JudgeUtil.in(type,DistributeConstant.LoanType.HOUSE))
            return "房抵";
        if(JudgeUtil.in(type,DistributeConstant.LoanType.CAR))
            return "车抵";
        if(JudgeUtil.in(type,DistributeConstant.LoanType.FUND))
            return "公积金贷";
        return "信贷";
    }


    /**线上产品统计*/
    private String jointOnlineLenderSummarizing(String startDate,String endDate){
        StringBuffer content = new StringBuffer();
        List<Map<String,Object>> onlineLenderSummations = orgService.getlineLenderCountSummation(startDate,endDate);
        if(CollectionUtil.isEmpty(onlineLenderSummations)){
           return content.toString();
        }
        content.append("<table border='1' cellspacing='0'>");
        content.append("<tr>")
                .append("<td>").append("线上产品名称").append("</td>")
                .append("<td>").append("点击申请数量").append("</td>")
                .append("</tr>");
        for(Map<String,Object> onlineLenderSummation : onlineLenderSummations) {
            content.append("<tr>")
                    .append("<td>").append(onlineLenderSummation.get("lenderName")).append("</td>")
                    .append("<td>").append(onlineLenderSummation.get("counts")).append("</td>")
                    .append("</tr>");
        }
        content.append("</table>").append("</br>");
        content.append("------------------------------------------------------------").append("</br>");
        return content.toString();
    }

    /**线下机构分发数据汇总*/
    private String jointOfflineOrgSummarizing(String startDate,String endDate,List<Map<String,Object>> summations){
        StringBuffer content = new StringBuffer();
//        List<Map<String,Object>> summations = orgService.getOrgDistributeCountSummation(startDate,endDate);
        if(CollectionUtil.isEmpty(summations)){
            log.info("暂时还没有分发汇总数据");
            return content.toString();
        }
        content.append("<table border='1' cellspacing='0'>");
        content.append("<tr>")
                .append("<td>").append("机构id").append("</td>")
                .append("<td>").append("机构名称").append("</td>")
                .append("<td>").append("机构城市").append("</td>")
                .append("<td>").append("限制时间").append("</td>")
                .append("<td>").append("每日限量").append("</td>")
                .append("<td>").append("分发成功数量").append("</td>")
                .append("<td>").append("预期收益").append("</td>")
                .append("<td>").append("被除重数").append("</td>")
                .append("<td>").append("其他失败数").append("</td>")
                .append("<td>").append("分发总数").append("</td>")
                .append("<td>").append("高分客户数量").append("</td>")
                .append("<td>").append("分发成功占比").append("</td>")
                .append("<td>").append("被除重占比").append("</td>")
                .append("<td>").append("其他失败占比").append("</td>")
                .append("<td>").append("高分客户占分发成功比").append("</td>")
                .append("</tr>");
        int count = 0;
        int totalSuccessCount = 0;
        BigDecimal income = new BigDecimal(0);
//        Map<String, CitySummarizingBO> cityMap = new HashMap<>();
        for(Map<String,Object> summation : summations) {
            if(null == summation)
                continue;
            int limitCount = Integer.valueOf(summation.get("每日限量") == null ? "0":summation.get("每日限量").toString());
            int successCount = Integer.valueOf((summation.get("分发成功数") == null? "0":summation.get("分发成功数").toString()));
            if(limitCount <= 0 && successCount <=0 )
                continue;
            Object city = summation.get("机构城市");
            city = (null == city || "全国".equals(city)) ? "全国":city.toString().split(",").length > 1 ? "":city.toString();
            BigDecimal orgIncome = new BigDecimal(summation.get("预期收益")== null?"0":summation.get("预期收益").toString());
            if(!JudgeUtil.in(Integer.valueOf(summation.get("org_id").toString()),11,12)){
                count += limitCount;
                totalSuccessCount += successCount;
                income = income.add(orgIncome);
            }
            content.append("<tr>")
                    .append("<td>").append(summation.get("org_id")).append("</td>")
                    .append("<td>").append(summation.get("机构名称")).append("</td>")
                    .append("<td>").append(city).append("</td>")
                    .append("<td>").append(summation.get("限制时间")).append("</td>")
                    .append("<td>").append(limitCount).append("</td>")
                    .append("<td>").append(summation.get("分发成功数")).append("</td>")
                    .append("<td>").append(summation.get("预期收益")).append("</td>")
                    .append("<td>").append(summation.get("被除重数")).append("</td>")
                    .append("<td>").append(summation.get("其他失败数")).append("</td>")
                    .append("<td>").append(summation.get("分发总数")).append("</td>")
                    .append("<td>").append(summation.get("高分客户数量")).append("</td>")
                    .append("<td>").append(summation.get("分发成功占比")).append("</td>")
                    .append("<td>").append(summation.get("被除重占比")).append("</td>")
                    .append("<td>").append(summation.get("其他失败占比")).append("</td>")
                    .append("<td>").append(summation.get("高分客户数量占比")).append("</td>")
                    .append("</tr>");
        }
        content.append("<tr>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("合计：").append("</td>")
                .append("<td>").append(count).append("</td>")
                .append("<td>").append(totalSuccessCount).append("</td>")
                .append("<td>").append(income.doubleValue()).append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("<td>").append("-").append("</td>")
                .append("</tr>");
        content.append("</table>").append("</br>");
        content.append("------------------------------------------------------------").append("</br>");
        return content.toString();
    }

    /**线下分发统计分城市*/
    private String jointOfflineOrgSummarizingDifferentiateCity(String startDate,String endDate){
        StringBuffer content = new StringBuffer();
        //线下分发分城市统计
        List<OrgDistributeStatisticsBO> statisticsList = orgService.getOrgDistributeStatistics(startDate, endDate);
        if(CollectionUtil.isEmpty(statisticsList)){
            log.info("暂时还没有分发成功的数据");
            return content.toString();
        }
        content.append("<table border='1' cellspacing='0'>");
        content.append("<tr>")
                .append("<td>").append("机构id").append("</td>")
                .append("<td>").append("机构名称").append("</td>")
                .append("<td>").append("机构城市").append("</td>")
                .append("<td>").append("限制数量").append("</td>")
                .append("<td>").append("分发数量").append("</td>")
                .append("</tr>");
        for(OrgDistributeStatisticsBO statistics : statisticsList) {

            content.append("<tr>")
                    .append("<td>").append(statistics.getOrgId()).append("</td>")
                    .append("<td>").append(statistics.getOrgName()).append("</td>")
                    .append("<td>").append(statistics.getCity()).append("</td>");
            Integer limitCount = statistics.getLimitCount();
            if(null == limitCount){
                content.append("<td>").append("无限制").append("</td>");
            }else{
                content.append("<td>").append(limitCount).append("</td>");
            }
            content.append("<td>").append(statistics.getCounts()).append("</td>")
                    .append("</tr>");
        }
        content.append("</table>").append("</br>");
        content.append("------------------------------------------------------------").append("</br>");
        content.append("------------------------------------------------------------").append("</br>");
        return content.toString();
    }

}
