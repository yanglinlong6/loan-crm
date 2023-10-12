package com.crm.service.channel;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.channel.dao.ChannelMapper;
import com.crm.service.channel.dao.ChannelPriceMapper;
import com.crm.service.channel.dao.ChannelRationMapper;
import com.crm.service.channel.dao.ChannelReportMapper;
import com.crm.service.channel.model.*;
import com.crm.service.role.model.RolePO;
import com.crm.util.DateUtil;
import com.crm.util.ListUtil;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 渠道service
 */
@Service
public class ChannelServiceImpl implements ChannelService{

    @Autowired
    ChannelMapper channelMapper;

    @Autowired
    ChannelRationMapper channelRationMapper;

    @Autowired
    ChannelPriceMapper channelPriceMapper;

    @Override
    public void getChannelPage(PageBO<ChannelPO> page) {
        if(null == page)
            return;
        if(page.getParamObject() == null){
            ChannelPO channel = new ChannelPO();
            channel.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            page.setParamObject(channel);
        }else
            page.getParamObject().setOrgId(LoginUtil.getLoginEmployee().getOrgId());

        page.setDataList(channelMapper.selectChannelPage(page));
        int totalCount = channelMapper.selectChannelPageCount(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public List<ChannelPO> getAllChannel() {
        RolePO role = LoginUtil.getLoginEmployee().getRole();
        if(null != role && CrmConstant.Role.Type.CHANNEL == role.getType()){
            return channelMapper.selectAll(LoginUtil.getLoginEmployee().getOrgId(),LoginUtil.getLoginEmployee().getChannelId());
        }
        return channelMapper.selectAll(LoginUtil.getLoginEmployee().getOrgId(),null);
    }

    @Override
    public void addChannel(ChannelPO channel) {
        if(null == channel || StringUtils.isBlank(channel.getNickname()) || StringUtils.isBlank(channel.getCompany())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"缺少渠道名称或者公司名称！");
        }
        channel.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        ChannelPO po = channelMapper.selectChannelByNicknameOrCompany(channel.getOrgId(),channel.getNickname(),channel.getCompany());
        if(null != po){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"渠道名称 或者 渠道公司名称 已存在！");
        }

        channel.setKey(UUID.randomUUID().toString().replaceAll("-",""));
        channel.setCreateBy(LoginUtil.getLoginEmployee().getName());
        channelMapper.insertChannel(channel);
    }

    @Override
    public void updateChannel(ChannelPO channel) {
        if(null == channel || StringUtils.isBlank(channel.getNickname()) || StringUtils.isBlank(channel.getCompany())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"缺少渠道名称或者公司名称！");
        }
        channel.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        ChannelPO po = channelMapper.selectChannelByNicknameOrCompany(channel.getOrgId(),channel.getNickname(),channel.getCompany());
        if(null != po && channel.getId() != po.getId()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"渠道名称 或者 渠道公司名称 已存在！");
        }
        channel.setKey(null);
        channel.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        channelMapper.updateBChannel(channel);
    }

    @Override
    public void delChannel(ChannelPO channelPO) {
        if(null == channelPO || null == channelPO.getId()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"渠道id不能为空");
        }
        channelMapper.delChannel(channelPO.getId());
    }

    @Override
    public ChannelPO getChannel(Long id) {
        return channelMapper.selectChannelById(id);
    }

    @Override
    public void getChannelRationPage(PageBO<ChannelRationBO> page) {
        if(null == page)
            return;
        if(page.getParamObject() == null){
            ChannelRationBO bo = new ChannelRationBO();
            bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            page.setParamObject(bo);
        }else{
            ChannelRationBO bo = page.getParamObject();
            bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            if(null == bo.getChannelId() || bo.getChannelId() <= 0){
                bo.setChannel(null);
            }
            if(StringUtils.isBlank(bo.getCity()))
                bo.setCity(null);
            if(StringUtils.isBlank(bo.getMedia()))
                bo.setMedia(null);
            page.setParamObject(bo);
        }
        page.setDataList(channelRationMapper.selectChannelRationPage(page));
        int totalCount = channelRationMapper.selectChannelRationPageCount(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public void addChannelRation(ChannelRationPO channelRationPO) {
        if(null == channelRationPO || null == channelRationPO.getChannelId() || StringUtils.isBlank(channelRationPO.getMedia()) || StringUtils.isBlank(channelRationPO.getCity())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"配量管理:缺少渠道id 或者 媒体名称 或者 城市");
        }
        ChannelRationPO po = channelRationMapper.selectChannelRationPO(channelRationPO.getChannelId(),channelRationPO.getCity(),channelRationPO.getMedia());
        if(null != po){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"配量管理:渠道id,城市,媒体三者一起不能重复!");
        }
        channelRationPO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        channelRationPO.setCreateBy(LoginUtil.getLoginEmployee().getName());
        channelRationMapper.insertChannelRation(channelRationPO);
    }

    @Override
    public void updatChannelRation(ChannelRationPO channelRationPO) {
        if(null == channelRationPO || null == channelRationPO.getChannelId() || StringUtils.isBlank(channelRationPO.getMedia()) || StringUtils.isBlank(channelRationPO.getCity())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"配量管理:缺少渠道id 或者 媒体名称 或者 城市");
        }
        ChannelRationPO po = channelRationMapper.selectChannelRationPO(channelRationPO.getChannelId(),channelRationPO.getCity(),channelRationPO.getMedia());
        if(null != po && channelRationPO.getId() != po.getId() ){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"配量管理:渠道id,城市,媒体三者一起不能重复!");
        }
        channelRationPO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        channelRationMapper.updateChannelRationPO(channelRationPO);
    }


    @Autowired
    ChannelReportMapper channelReportMapper;

    @Override
    public void getChannelReportList(PageBO<ChannelReportBO> page) {
        if(null == page){
            return;
        }
        ChannelReportBO reportBO = page.getParamObject();
        if(reportBO == null){
            page.setParamObject(reportBO);
        }
        if(StringUtils.isBlank(reportBO.getChannel()))
            reportBO.setChannel(null);
        if(StringUtils.isBlank(reportBO.getMedia()))
            reportBO.setMedia(null);
        if(StringUtils.isBlank(reportBO.getCity()))
            reportBO.setCity(null);
        reportBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        // 如果是渠道经理，则只能看当前渠道的报表
        if(null != LoginUtil.getLoginEmployee().getChannelId() && LoginUtil.getLoginEmployee().getChannelId() >0)
            reportBO.setChannelId(LoginUtil.getLoginEmployee().getChannelId());
        // 计算日期
        String startDate = DateUtil.cumputeStartDate(page.getParamObject().getStartDate());
        String endDate = DateUtil.computeEndDate(page.getParamObject().getEndDate());
        reportBO.setStartDate(startDate);
        reportBO.setEndDate(endDate);
        page.setParamObject(reportBO);
        List<ChannelReportBO> datas = channelReportMapper.getChannelReportByPage(page);
        if(ListUtil.isEmpty(datas)){
            return;
        }
        for(ChannelReportBO bo : datas){
            bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
//            bo.setLevel0Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.ZERO));
//            bo.setLevel1Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.ONE));
//            bo.setLevel2Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.TWO));
//            bo.setLevel25Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.TWO_FIVE));
//            bo.setLevel3Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.THREE));
//            bo.setLevel4Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.FOUR));
//            bo.setLevel5Count(channelReportMapper.selectLevelCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Level.FIVE));
            bo.setFixCount(channelReportMapper.selectFitCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Fit.FIT));
            bo.setCallCount(channelReportMapper.selectCallCount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate,CrmConstant.Customer.Call.CALL));
            bo.setContractAmount(channelReportMapper.selectContractAmount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate));// 汇总合同金额
            bo.setIncomeAmount(channelReportMapper.selectImportAmount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate));// 汇总进件金额
            bo.setSurplus(bo.getContractAmount()-bo.getIncomeAmount());//计算未收金额
            bo.setConsumeAmount(channelReportMapper.selectChannelConsumeAmount(bo.getOrgId(),bo.getChannelId(),bo.getMedia(),bo.getCity(),startDate,endDate));//渠道消费金额
            if(bo.getIncomeAmount() == 0d && bo.getConsumeAmount() == 0d ){
                bo.setConsumeIncomeRate("0:0");
                continue;
            }
            if(bo.getIncomeAmount() == 0d){
                bo.setConsumeIncomeRate("0:"+bo.getConsumeAmount());
                continue;
            }
            if(bo.getConsumeAmount() == 0d){
                bo.setConsumeIncomeRate(bo.getIncomeAmount()+":0");
                continue;
            }
            double value = BigDecimal.valueOf(bo.getIncomeAmount()).divide(BigDecimal.valueOf(bo.getConsumeAmount()),2,BigDecimal.ROUND_HALF_UP).doubleValue();
            bo.setConsumeIncomeRate("1:"+value);

        }
        page.setDataList(datas);
        int totalCount = channelReportMapper.getChannelReportCountByPage(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public List<ChannelPricePO> getAllChannelPrice(ChannelPricePO channelPrice) {
        if(null == channelPrice)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询配量成本失败：缺少参数");
        if(null == channelPrice.getChannelId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询配量成本失败：缺少渠道id");
        if(null == channelPrice.getRationId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询配量成本失败：缺少配量id");
        if(StringUtils.isBlank(channelPrice.getCity()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询配量成本失败：缺少配量城市");
        if(StringUtils.isBlank(channelPrice.getMedia()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询配量成本失败：缺少配量媒体");

        channelPrice.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        if(StringUtils.isBlank(channelPrice.getStartDate())){
            channelPrice.setStartDate(DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd));
        }

        channelPrice.setEndDate(DateUtil.computeEndDate(channelPrice.getEndDate()));
        return channelPriceMapper.selectAllChannelPrice(channelPrice);
    }


    @Override
    public void addChannelPrice(ChannelPricePO channelPrice) {
        if(null == channelPrice)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少参数");
        if(null == channelPrice.getRationId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少配量id");
        if(null == channelPrice.getChannelId())
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少渠道id");
        if(StringUtils.isBlank(channelPrice.getMedia()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少媒体");
        if(StringUtils.isBlank(channelPrice.getCity()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少城市");
        if(StringUtils.isBlank(channelPrice.getInputDate()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少日期（例如：2021-06-20）");
        if(null == channelPrice.getPrice() || channelPrice.getPrice().doubleValue() < 0d)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"录入成本失败：缺少成本单价");
        channelPrice.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        channelPrice.setStartDate(DateUtil.cumputeStartDate(channelPrice.getStartDate()));
        channelPrice.setEndDate(DateUtil.computeEndDate(channelPrice.getEndDate()));
        if(!ListUtil.isEmpty(channelPriceMapper.selectAllChannelPrice(channelPrice))){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"渠道,媒体,城市,日期[请勿重新录入成本]");
        }
        channelPrice.setCreateBy(LoginUtil.getLoginEmployee().getName());
        channelPriceMapper.insertChannelPrice(channelPrice);
    }

    @Override
    public void updateChannelPrice(ChannelPricePO channelPrice) {
        if(null == channelPrice || null == channelPrice.getId()){
            return;
        }
        channelPriceMapper.updateChannelPrice(channelPrice);
    }

    @Override
    public void delChannelPrice(Long id) {
        if(null == id || id <= 0){
            return;
        }
        channelPriceMapper.delChannelPrice(id);
    }

}
