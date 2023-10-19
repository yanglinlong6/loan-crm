package com.help.loan.distribute.service.orgOffer;

import com.help.loan.distribute.common.DistributeConstant;
import com.help.loan.distribute.common.email.EmailBO;
import com.help.loan.distribute.common.email.EmailService;
import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.service.billRec.BillRecService;
import com.help.loan.distribute.service.billRec.model.BillRecPO;
import com.help.loan.distribute.service.orgOffer.dao.OrgAccountDao;
import com.help.loan.distribute.service.orgOffer.dao.OrgCityOfferBillsDao;
import com.help.loan.distribute.service.orgOffer.dao.OrgOrderDao;
import com.help.loan.distribute.service.orgOffer.model.OrgAccountPO;
import com.help.loan.distribute.service.orgOffer.model.OrgCityOfferBillsPO;
import com.help.loan.distribute.service.orgOffer.model.OrgOrderPO;
import com.help.loan.distribute.service.sms.AliyunSms;
import com.help.loan.distribute.service.sms.SmsApi;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrgBillsServiceImpl implements OrgBillsService {

    private static final Logger log = LoggerFactory.getLogger(OrgBillsServiceImpl.class);

    @Autowired
    OrgCityOfferBillsDao orgCityOfferBillsDao;

    @Autowired
    OrgAccountDao orgAccountDao;

    @Autowired
    OrgOrderDao orgOrderDao;

    @Autowired
    BillRecService billRecService;


    @Override
    public void bills(String dateStr) throws InterruptedException {

        if(StringUtils.isBlank(dateStr) || !dateStr.contains("-")){
            dateStr = DateUtil.formatToString(new Date(),"yyyy-MM-dd");
        }
        String startDate = dateStr + " 00:00:00";
        String endDate = dateStr +" 23:59:59";
        String dateNum = dateStr.replace("-","");
        if(StringUtils.isBlank(dateNum))
            return;
        BillRecPO billRec = billRecService.getBillRecByBillDate(dateNum);
        if(null != billRec && DistributeConstant.BillRec.Status.ACCOMPLISH == billRec.getStatus().byteValue()){
            log.error("[{}]已完成结算,请勿再次结算!",dateNum);
            return;
        }
        //如果已经存在，则删除
        List<OrgCityOfferBillsPO> list = orgCityOfferBillsDao.selectAllByDateNum(dateNum,startDate,endDate);
        if(CollectionUtil.isEmpty(list))
            return;
        Map<Long,List<OrgCityOfferBillsPO>> map = new HashMap<>();
        for(OrgCityOfferBillsPO orgCityOfferBillsPO : list){
            Long orgId = orgCityOfferBillsPO.getOrgId();
            if(map.containsKey(orgId)){
                map.get(orgId).add(orgCityOfferBillsPO);
            }else {
                List<OrgCityOfferBillsPO> newList = new ArrayList<>();
                newList.add(orgCityOfferBillsPO);
                map.put(orgId,newList);
            }
        }
        processOrgBills(dateStr,map);//处理结算
    }

    private void processOrgBills(String dateStr,Map<Long,List<OrgCityOfferBillsPO>> map) throws InterruptedException {
        Set<Long> keySet = map.keySet();
        for(Long orgId : keySet) {
            Thread.sleep(1000L);
            processOrgBills(dateStr, orgId, map.get(orgId));
        }
        log.info("{}-结算数据执行完成，结算机构数量：{}",dateStr,keySet.size());
    }

    private void processOrgBills(String dateStr,Long orgId,List<OrgCityOfferBillsPO> list){
        OrgAccountPO orgAccount = orgAccountDao.selectByOrgId(orgId);
        if(null == orgAccount){
            log.info("机构:{}-未配置账户信息【跳过】",orgId);
            return;
        }
        //计算消耗金额，拼接短信内容
        BigDecimal amount = new BigDecimal(0);
        StringBuffer content = new StringBuffer();
        for(OrgCityOfferBillsPO orgBills : list){
            content.append(orgBills.getCity()).append(orgBills.getWindUpPrice()).append(",");
            amount = amount.add(orgBills.getWindAmount());
        }
        orgAccount.setRemainingAmount(orgAccount.getRemainingAmount().subtract(amount));
        sendSms(orgAccount,dateStr,content.toString()); // 发送手机提醒消息
//        boolean send = sendEmail(orgAccount,dateStr);//发送邮件
//        log.info("机构:{}-{},邮件发送状态:{}",orgAccount.getOrgId(),orgAccount.getOrgNickname(),send);
        orgAccountDao.updateByOrgId(orgAccount);
//        if(send){//发送失败,不更新账户余额
//            orgAccountDao.updateByOrgId(orgAccount);
//            log.info("机构：{}-{}，结算数据发送成功",orgAccount.getOrgId(),orgAccount.getOrgNickname());
//        }else
//            log.error("机构：{}-{}，结算数据结算失败",orgAccount.getOrgId(),orgAccount.getOrgNickname());
    }

    /**发送短信提醒消息*/
    private void sendSms(OrgAccountPO orgAccount,String dateStr,String mobileContent){
        if(StringUtils.isBlank(orgAccount.getMobile())){
            return;
        }
//        mobileContent = mobileContent+"详细看邮件";
        SmsApi smsApi = new AliyunSms();
        if(orgAccount.getMobile().contains(",")){
            String[] mobiles = orgAccount.getMobile().split(",");
            for(String  mobile : mobiles){
                smsApi.sendMessage(mobile,dateStr,orgAccount.getRemainingAmount(),mobileContent);
            }
        }else
            smsApi.sendMessage(orgAccount.getMobile(),dateStr,orgAccount.getRemainingAmount(),mobileContent);
    }

    private boolean sendEmail(OrgAccountPO orgAccount,String dateStr){
        if(StringUtils.isBlank(orgAccount.getEmail()))
            return true;
        String subject = dateStr+"结算数据【"+orgAccount.getOrgNickname()+"】";//邮件标题
        StringBuffer orgAccountContent = new StringBuffer();//机构账户余额
        orgAccountContent.append("<table border='1' cellspacing='0'>");
        orgAccountContent
                .append("<tr>")
                .append("<td>").append("公司简称").append("</td>")
                .append("<td>").append("余额").append("</td>")
                .append("</tr>");
        orgAccountContent
                    .append("<tr>")
                    .append("<td>").append(orgAccount.getOrgNickname()).append("</td>")
                    .append("<td>").append(orgAccount.getRemainingAmount()).append("</td>")
                    .append("</tr>");
        orgAccountContent.append("</table>");

        String[] dateArray = dateStr.split("-");
        String year = dateArray[0];
        String month = dateArray[1];
        String startDate = year+"-"+month+"-"+"01 00:00:00";
        //当月充值记录
        List<OrgOrderPO> orders = orgOrderDao.selectAllByOrgId(orgAccount.getOrgId(),startDate);
        if(!CollectionUtil.isEmpty(orders)){
            orgAccountContent.append("------------------------------------------------------------------------------------------------");
            //当月预付记录
            StringBuffer orgAccountOrder = new StringBuffer("<table border='1' cellspacing='0'>");
            orgAccountOrder
                    .append("<tr>")
                    .append("<td>").append("公司简称").append("</td>")
                    .append("<td>").append("预付金额").append("</td>")
                    .append("<td>").append("日期").append("</td>")
                    .append("</tr>");
            for(OrgOrderPO orgOrder : orders){
                orgAccountOrder
                        .append("<tr>")
                        .append("<td>").append(orgOrder.getOrgNickname()).append("</td>")
                        .append("<td>").append(orgOrder.getAmount()).append("</td>")
                        .append("<td>").append(DateUtil.formatToString(orgOrder.getCreateDate(),DateUtil.yyyyMMdd)).append("</td>")
                        .append("</tr>");
            }
            orgAccountOrder.append("</table>");
            orgAccountContent.append(orgAccountOrder);
        }

        //当月消费记录
        String endDate = dateStr +" 23:59:59";
       List<OrgCityOfferBillsPO> orgCityOfferBillsList = orgCityOfferBillsDao.selectAllByOrgIdAndDate(orgAccount.getOrgId(),startDate,endDate);
        if(!CollectionUtil.isEmpty(orgCityOfferBillsList)){
            orgAccountContent.append("------------------------------------------------------------------------------------------------");
            StringBuffer orgBillsContent = new StringBuffer("<table border='1' cellspacing='0'>");
            orgBillsContent
                    .append("<tr>")
                    .append("<td>").append("公司简称").append("</td>")
                    .append("<td>").append("日期").append("</td>")
                    .append("<td>").append("城市").append("</td>")
                    .append("<td>").append("消耗数量").append("</td>")
                    .append("<td>").append("结算价").append("</td>")
                    .append("<td>").append("结算金额").append("</td>")
                    .append("<td>").append("备注信息").append("</td>")
                    .append("</tr>");
            for(OrgCityOfferBillsPO bills : orgCityOfferBillsList){
                orgBillsContent
                        .append("<tr>")
                        .append("<td>").append(bills.getOrgName()).append("</td>")
                        .append("<td>").append(bills.getDateNum()).append("</td>")
                        .append("<td>").append(bills.getCity()).append("</td>")
                        .append("<td>").append(bills.getWindCount()).append("</td>")
                        .append("<td>").append(bills.getWindUpPrice()).append("</td>")
                        .append("<td>").append(bills.getWindAmount()).append("</td>")
                        .append("<td>").append(bills.getRemark()).append("</td>")
                        .append("</tr>");
            }
            orgBillsContent.append("</table>");
            orgAccountContent.append(orgBillsContent);
        }
        try{
            String email = orgAccount.getEmail()+",wangping@bangzheng100.com,wangyang@bangzheng100.com";
            EmailService.sendMessage(new EmailBO(subject,orgAccountContent.toString(),email));
            return true;
        }catch (Exception e){
            log.error("【结算邮件】发送失败：{}-{},{}",orgAccount.getOrgId(),orgAccount.getOrgNickname(),e.getMessage(),e);
            return false;
        }
    }





}
