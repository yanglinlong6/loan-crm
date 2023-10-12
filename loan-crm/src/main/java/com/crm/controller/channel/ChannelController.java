package com.crm.controller.channel;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.channel.ChannelService;
import com.crm.service.channel.MediaService;
import com.crm.service.channel.dao.ChannelPriceMapper;
import com.crm.service.channel.model.*;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.model.CustomerBO;
import com.crm.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渠道接口
 *
 */
@RestController
public class ChannelController {

    private static final Logger log = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    ChannelService channelService;

    @PostMapping("/channel/page")
    @ResponseBody
    public ResultVO page(@RequestBody() PageBO<ChannelPO> pageBO){
        channelService.getChannelPage(pageBO);
        return ResultVO.success("渠道分页列表成功",pageBO);
    }

    @PostMapping("/channel/list")
    @ResponseBody
    public ResultVO all(){
        List<ChannelPO> data= channelService.getAllChannel();
        return ResultVO.success("",data);
    }

    @PostMapping("/channel/add")
    @ResponseBody
    public ResultVO add(@RequestBody()ChannelPO channelPO){
        channelService.addChannel(channelPO);
        return ResultVO.success("新增渠道成功",null);
    }


    @PostMapping("/channel/update")
    @ResponseBody
    public ResultVO update(@RequestBody()ChannelPO channelPO){
        channelService.updateChannel(channelPO);
        return ResultVO.success("更新渠道成功",null);
    }



    @PostMapping("/channel/del")
    @ResponseBody
    public ResultVO del(@RequestBody()ChannelPO channelPO){
        channelService.delChannel(channelPO);
        return ResultVO.success("删除渠道成功",null);
    }


    @Autowired
    CacheConfigService cacheConfigService;

    /**
     * 获取全部媒体
     * @return ResultVO
     */
    @PostMapping("/channel/media")
    @ResponseBody
    public ResultVO getAllMedia(){
        String[] array = MediaService.getAll(LoginUtil.getLoginEmployee().getOrgId().toString());
        return ResultVO.success("获取全部媒体成功", array);
    }


    //   以下是渠道配量管理接口

    /**
     * 配量管理-分页
     * @param pageBO PageBO<ChannelRationBO>
     * @return ResultVO
     */
    @PostMapping("/channel/ration/page")
    @ResponseBody
    public ResultVO channelRationPage(@RequestBody()PageBO<ChannelRationBO> pageBO){
        channelService.getChannelRationPage(pageBO);
        return ResultVO.success("渠道配量列表成功",pageBO);
    }

    @PostMapping("/channel/ration/add")
    @ResponseBody
    public ResultVO channelRationAdd(@RequestBody()ChannelRationPO channelRationPO){
        channelService.addChannelRation(channelRationPO);
        return ResultVO.success("渠道配量列表成功",channelRationPO);
    }

    @PostMapping("/channel/ration/update")
    @ResponseBody
    public ResultVO channelRationUpdate(@RequestBody()ChannelRationPO channelRationPO){
        channelService.updatChannelRation(channelRationPO);
        return ResultVO.success("渠道配量列表成功",channelRationPO);
    }





    @PostMapping("/channel/report/page")
    @ResponseBody
    public ResultVO report(@RequestBody() PageBO<ChannelReportBO> pageBO){
        channelService.getChannelReportList(pageBO);
        pageBO.setParamObject(null);
        return ResultVO.success("获取渠道报表成功",pageBO);
    }


    @Autowired
    CustomerService customerService;

    /**
     * 渠道管理：渠道客户分页
     * @return
     */
    @PostMapping("/channel/customer/page")
    @ResponseBody
    public ResultVO channelCustomerPage(@RequestBody() PageBO<CustomerBO> pageBO){
        if(null == pageBO || null == LoginUtil.getLoginEmployee().getChannelId())
            return ResultVO.success("渠道客户列表-分页成功",pageBO);
        CustomerBO customerBO = pageBO.getParamObject();
        if(null == customerBO)
            customerBO = new CustomerBO();
        customerBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        customerBO.setStartDate(DateUtil.cumputeStartDate(customerBO.getStartDate()));
        customerBO.setEndDate(DateUtil.computeEndDate(customerBO.getEndDate()));
        customerBO.setChannel(LoginUtil.getLoginEmployee().getChannelId());
        pageBO.setParamObject(customerBO);
        customerService.getCustomerPage(pageBO);
        return ResultVO.success("渠道客户列表-分页成功",pageBO);
    }


    /**
     * 渠道配量成本列表
     * @param channelPricePO ChannelPricePO
     * @return ResultVO
     */
    @PostMapping("/channel/price/all")
    @ResponseBody
    public ResultVO allChannelPrice(@RequestBody()ChannelPricePO channelPricePO){
        return ResultVO.success("渠道配量管理-全部成本成功",channelService.getAllChannelPrice(channelPricePO));
    }

    @PostMapping("/channel/add/price")
    @ResponseBody
    public ResultVO addChannelPrice(@RequestBody()ChannelPricePO channelPricePO){
        channelService.addChannelPrice(channelPricePO);
        return ResultVO.success("渠道配量管理-录入成本成功",null);
    }

    @PostMapping("/channel/update/price")
    @ResponseBody
    public ResultVO updateChannelPrice(@RequestBody()ChannelPricePO channelPricePO){
        channelService.updateChannelPrice(channelPricePO);
        return ResultVO.success("渠道配量管理-修改成本成功",null);
    }

    @PostMapping("/channel/del/price")
    @ResponseBody
    public ResultVO delChannelPrice(@RequestParam("id") Long id){
        channelService.delChannelPrice(id);
        return ResultVO.success("渠道配量管理-删除成本成功",null);
    }



}
