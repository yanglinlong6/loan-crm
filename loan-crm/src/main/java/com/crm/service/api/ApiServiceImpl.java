package com.crm.service.api;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.api.DistributeCustomerThread;
import com.crm.controller.login.LoginUtil;
import com.crm.service.api.model.ImportBO;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.channel.ChannelService;
import com.crm.service.channel.dao.ChannelRationMapper;
import com.crm.service.channel.model.ChannelPO;
import com.crm.service.channel.model.ChannelRationPO;
import com.crm.service.customer.CustomerService;
import com.crm.service.customer.dao.CustomerFieldMapper;
import com.crm.service.customer.model.CustomerPO;
import com.crm.service.employee.EmployeeMsgService;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.EmployeeMsgPO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.org.OrgService;
import com.crm.service.org.ShopService;
import com.crm.service.org.model.OrgPO;
import com.crm.service.org.model.ShopPO;
import com.crm.service.sms.SmsApi;
import com.crm.service.sms.WDSms;
import com.crm.service.sms.YunSms;
import com.crm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ApiServiceImpl implements ApiService{

    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceImpl.class);

    @Autowired
    ChannelService channelService;

    @Autowired
    ChannelRationMapper channelRationMapper;

    @Autowired
    CustomerService customerService;

    @Autowired
    ShopService shopService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    EmployeeMsgService employeeMsgService;

    @Autowired
    CustomerFieldMapper customerFieldMapper;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    OrgService orgService;

    @Autowired
    private CacheConfigService cacheConfigService;

    @Transactional
    @Override
    public void importCustomer(ImportBO importBO) {
        if(null == importBO || null == importBO.getChannelId() || importBO.getChannelId() <=0)
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少渠道参数");
        if( StringUtils.isBlank(importBO.getData()))
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少data参数");

        ChannelPO channel = channelService.getChannel(importBO.getChannelId());
        if(null == channel)
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"渠道不存在");

        String data = DESUtil.decrypt(channel.getKey(),importBO.getData());
        CustomerPO customer = JSONUtil.toJavaBean(data,CustomerPO.class);
        if(null == customer)
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"未提交客户参数");
        LOG.info("导入新客户：{}",customer.toString());

        String city = customer.getCity();
        if(StringUtils.isBlank(city))
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少城市");
        customer.setCity(parseCity(customer));// 设置真实城市

        if(StringUtils.isBlank(customer.getMedia()))
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少媒体");

        customer.setChannel(importBO.getChannelId());
        customer.setMobileMd5(MD5Util.getMd5String(customer.getMobile()));
        customer.setOrgId(channel.getOrgId());
        //撞库验证
        synchronized (this){
            CustomerPO md5 = customerService.getCustomerByMd5(customer.getOrgId(),customer.getMobileMd5());
            if(null != md5)
                throw  new CrmException(CrmConstant.ResultCode.EXIST,"客户手机号码已经存在");
        }
        // 验证该渠道量级是否已经满了
        ChannelRationPO channelRation = channelRationMapper.selectChannelRationPO(customer.getChannel(),city,customer.getMedia());
        if(null == channelRation)
            throw  new CrmException(CrmConstant.ResultCode.EXIST,"渠道,城市,媒体未配置");

        String dayStart = DateUtil.cumputeStartDate(DateUtil.getCurrentDate(DateUtil.yyyymmdd));
        int dayCount = customerService.getChannelNewCustomerCount(customer.getOrgId(),customer.getChannel(),city,channelRation.getMedia(),dayStart);
        if(dayCount >= channelRation.getDayCount()){
            String msg = "渠道:"+channel.getId()+",城市:"+city+",媒体:"+customer.getMedia()+"【当天量已满】";
            throw  new CrmException(CrmConstant.ResultCode.EXIST,msg);
        }
        String weekStart = DateUtil.computeWeekDay(new Date(),2,DateUtil.yyyymmdd)+CrmConstant.Date.START;
        int weekCount = customerService.getChannelNewCustomerCount(customer.getOrgId(),customer.getChannel(),city,channelRation.getMedia(),weekStart);
        if(weekCount >= channelRation.getWeekCount()){
            String msg = "渠道:"+channel.getId()+",城市:"+city+",媒体:"+customer.getMedia()+"【当周量已满】";
            throw  new CrmException(CrmConstant.ResultCode.EXIST,msg);
        }
        String monthStart = DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd)+CrmConstant.Date.START;
        int mouthCount = customerService.getChannelNewCustomerCount(customer.getOrgId(),customer.getChannel(),city,channelRation.getMedia(),monthStart);
        if(mouthCount >= channelRation.getMonthCount()){
            String msg = "渠道:"+channel.getId()+",城市:"+city+",媒体:"+customer.getMedia()+"【当月量已满】";
            throw  new CrmException(CrmConstant.ResultCode.EXIST,msg);
        }
        customerService.addCustomer(customer);

        OrgEmployeePO admin = employeeService.getAdminEmployee(customer.getOrgId());
        OrgPO orgPO = orgService.getOrgById(customer.getOrgId());

        try{
            SmsApi api = new YunSms();
            String msg = String.format(SmsApi.MESSAGE_TEMPLATE_DIS,(null == orgPO ? "邦证伟业": orgPO.getNickname()),customer.getName());
            if(null != admin){
                api.sendMessage(admin.getPhone(),msg,CrmConstant.CreateBy.IMPORT);
            }
            String customerMsg = String.format(SmsApi.MESSAGE_TEMPLATE_CUSTOMER,customerService.getZhuti(customer),customer.getName());
            api.sendMessage(customer.getMobile(),customerMsg,CrmConstant.CreateBy.CUSTOMER);
        }catch (Exception e){
            LOG.error("[提醒短信发送失败]{}",e.getMessage(),e);
        }
        new DistributeCustomerThread(this,customer.getOrgId()).run();

    }

    @Override
    public OrgEmployeePO importAccountingCustomer(ImportBO importBO) {
        if(null == importBO || null == importBO.getChannelId() || importBO.getChannelId() <=0)
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少渠道参数");
        if( StringUtils.isBlank(importBO.getData()))
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"缺少data参数");
        ChannelPO channel = channelService.getChannel(importBO.getChannelId());
        if(null == channel)
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"渠道不存在");
        String data = DESUtil.decrypt(channel.getKey(),importBO.getData());
        CustomerPO customer = JSONUtil.toJavaBean(data,CustomerPO.class);
        if(null == customer)
            throw  new CrmException(CrmConstant.ResultCode.FAIL,"未提交客户参数");
        customer.setOrgId(channel.getOrgId());
        checkParam(customer,customerFieldMapper);
        customer.setChannel(importBO.getChannelId());
        customer.setMobileMd5(MD5Util.getMd5String(customer.getMobile()));
        //撞库验证
        synchronized (this){
            CustomerPO md5 = customerService.getCustomerByMd5(customer.getOrgId(),customer.getMobileMd5());
            if(null != md5)
                throw  new CrmException(CrmConstant.ResultCode.EXIST,"客户手机号码已经存在");
        }

        // 验证该渠道量级是否已经满了
        ChannelRationPO channelRation = channelRationMapper.selectChannelRationPO(customer.getChannel(),customer.getCity(),customer.getMedia());
        if(null == channelRation)
            throw  new CrmException(CrmConstant.ResultCode.EXIST,"渠道,城市,媒体未配置");
        String dayStart = DateUtil.cumputeStartDate(DateUtil.getCurrentDate(DateUtil.yyyymmdd));
        int dayCount = customerService.getChannelNewCustomerCount(customer.getOrgId(),customer.getChannel(),channelRation.getCity(),channelRation.getMedia(),dayStart);
        if(dayCount >= channelRation.getDayCount()){
            String msg = "渠道:"+channel.getId()+",城市:"+customer.getCity()+",媒体:"+customer.getMedia()+"【当天量已满】";
            throw  new CrmException(CrmConstant.ResultCode.EXIST,msg);
        }
        String weekStart = DateUtil.computeWeekDay(new Date(),2,DateUtil.yyyymmdd)+CrmConstant.Date.START;
        int weekCount = customerService.getChannelNewCustomerCount(customer.getOrgId(),customer.getChannel(),channelRation.getCity(),channelRation.getMedia(),weekStart);
        if(weekCount >= channelRation.getWeekCount()){
            String msg = "渠道:"+channel.getId()+",城市:"+customer.getCity()+",媒体:"+customer.getMedia()+"【当周量已满】";
            throw  new CrmException(CrmConstant.ResultCode.EXIST,msg);
        }
        String monthStart = DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd)+CrmConstant.Date.START;
        int mouthCount = customerService.getChannelNewCustomerCount(customer.getOrgId(),customer.getChannel(),channelRation.getCity(),channelRation.getMedia(),monthStart);
        if(mouthCount >= channelRation.getMonthCount()){
            String msg = "渠道:"+channel.getId()+",城市:"+customer.getCity()+",媒体:"+customer.getMedia()+"【当月量已满】";
            throw  new CrmException(CrmConstant.ResultCode.EXIST,msg);
        }
        customer.setSex(Byte.valueOf(CrmConstant.Customer.init));
        customer.setNeed("0");
        customerService.addCustomer(customer);
        // 给管理员发送新客户到系统的提醒短息
        OrgEmployeePO admin = employeeService.getAdminEmployee(customer.getOrgId());

        OrgPO orgPO = orgService.getOrgById(customer.getOrgId());

        SmsApi api = new WDSms();
        String adminMsg = String.format(SmsApi.MESSAGE_TEMPLATE_DIS,(null == orgPO? "德务发法顾":orgPO.getNickname()),customer.getName());
        api.sendMessage(admin.getPhone(),adminMsg,CrmConstant.CreateBy.IMPORT);

        // 分配
        return distributeCustomer(customer);

    }

    @Override
    public OrgEmployeePO distributeCustomer(CustomerPO customer) {
       List<OrgEmployeePO> employeePOList =  employeeService.getAllEmployee(customer.getOrgId(),null,null);
       if(ListUtil.isEmpty(employeePOList)){
           return null;
       }
       Iterator<OrgEmployeePO> it = employeePOList.iterator();
       while (it.hasNext()){
           OrgEmployeePO employeePO = it.next();
           ShopPO shopPO = shopService.getShop(employeePO.getShopId());
           if(null == shopPO || !customer.getCity().equals(shopPO.getCity())){
               LOG.info("分配客户：员工-{}【非{}】",employeePO.getCount(),customer.getCity());
               it.remove();
               continue;
           }
           if(employeePO.getCount().intValue() <=0){
               LOG.info("分配客户：员工-{},每日限量-{}【每日限量0】",employeePO.getCount());
               it.remove();
               continue;
           }
           if(CrmConstant.Employee.Status.NO == employeePO.getStatus().byteValue()){
               LOG.info("分配客户：员工-{},是否在职-{}【已离职】",employeePO.getStatus());
               it.remove();
               continue;
           }
           if(CrmConstant.Employee.Receive.NO == employeePO.getReceive().byteValue()){
               LOG.info("分配客户：员工-{},接受状态-{}【不接受新客户】",employeePO.getReceive());
               it.remove();
               continue;
           }
           String key = CrmConstant.Config.Distribute.DIS_EMPLOYEE+employeePO.getPhone();
           if(stringRedisTemplate.opsForValue().getOperations().hasKey(key)){
               employeePO.setDistributeCount(Integer.valueOf(stringRedisTemplate.opsForValue().get(key)));
           }else{
               Integer count = customerService.getEmployeeDistributeCount(customer.getOrgId(),employeePO.getId(),DateUtil.cumputeStartDate(DateUtil.getCurrentDate(DateUtil.yyyymmdd)));
               stringRedisTemplate.opsForValue().set(key,count.toString(),1, TimeUnit.HOURS);
               employeePO.setDistributeCount(count);
           }
           if(employeePO.getCount() <=employeePO.getDistributeCount() ){
               it.remove();
           }
       }
        Collections.sort(employeePOList,(o1,o2)->{return o1.getDistributeCount()-o2.getDistributeCount();});
        OrgEmployeePO employee = employeePOList.get(0);
        customer.setShopId(employee.getShopId());
        customer.setTeamId(employee.getTeamId());
        customer.setEmployeeId(employee.getId());
        customer.setDistributeDate(new Date());
        customer.setProgress(CrmConstant.Customer.Progress.IS_DIS);
        customer.setFirstEmployee(employee.getId());
        customerService.updateCustomer(customer);
        customerService.addCustomerDistributeRec(customer.getId(),employee.getId(),CrmConstant.Customer.DisStatus.NEW,"distribute");
        LOG.info("客户：{}-{}，分配给了：{}-{}-{}",customer.getName(),customer.getMobile(),employee.getName(),employee.getPhone(),employee.getDistributeCount());

        // 发送提醒短信
        OrgPO orgPO = orgService.getOrgById(customer.getOrgId());
        String employeeMsg = String.format(SmsApi.MESSAGE_TEMPLATE_DIS,null == orgPO ? "德务发法顾" : orgPO.getNickname(),customer.getName());
        new WDSms().sendMessage(employee.getPhone(),employeeMsg,CrmConstant.CreateBy.DIS);

        // 更新发送数量
        String key = CrmConstant.Config.Distribute.DIS_EMPLOYEE+employee.getPhone();
        Integer count = employee.getDistributeCount()+1;
        stringRedisTemplate.opsForValue().set(key,count.toString(),2, TimeUnit.HOURS);

        // 页面提醒消息
        employeeMsgService.addMsg(new EmployeeMsgPO(employee.getId(),employee.getPhone(),String.format(SmsApi.MSG_TEMPLATE_ADMIN,customer.getName()),CrmConstant.Employee.Msg.Status.NEW));

        // 返回分配给哪个员工
       return employee;
    }

    @Override
    public void distributeCustomer(Long orgId) {
        String id = orgId.toString().intern();
        synchronized (id){
            OrgPO orgPO = orgService.getOrgById(orgId);
            if(null == orgPO){
                LOG.error("机构[{}]不存在!",orgId);
                return;
            }
            if(CrmConstant.Org.Auto.Y != orgPO.getAutomatic().byteValue()){
                LOG.error("机构[{}-{}]未开启自动分配",orgId,orgPO.getNickname());
                return;
            }
            //获取待分配列表
            List<CustomerPO> customerList = customerService.getNewCustomerList(orgId);
            if(ListUtil.isEmpty(customerList)){
                LOG.info("机构[{}-{}]没有新客户",orgId,orgPO.getNickname());
                return;
            }
            // 获取机构下已经登录的账号
            Set<String> keys = stringRedisTemplate.opsForValue().getOperations().keys(LoginUtil.TOKEN_VALUE_PREFIX+orgId+"_*");
            if(ListUtil.isEmpty(keys)){
                LOG.info("机构【{}】，没有业务顾问登录",orgId);
                return;
            }
            Set<String> loginMobileList = new HashSet<>();
            for(String key : keys){
                if(LOG.isDebugEnabled())
                    LOG.info("机构【{}】,登陆员工token-{}",orgId,key);
                String[] array = key.split("_");
                loginMobileList.add(array[3]);
            }
            if(ListUtil.isEmpty(loginMobileList)){
                LOG.info("机构【{}】,没有员工登陆",orgId);
                return;
            }
            Map<String,List<OrgEmployeePO>> map = new HashMap<>();
            for(CustomerPO customer : customerList){
                if(null == customer)
                    continue;
                String city = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CITY,customer.getOrgId().toString());//判断机构需求城市
                if(!JudgeUtil.in(city,CrmConstant.ALL_CITY)){ // 如果机构需求客户城市是全国,则客户城市变成全国
                    city = customer.getCity();
                }
                // 获取该机构下 当前城市的所有门店下的员工
                getOrgCityEmployeeList(customer,map);
                // 分配
                boolean isSuccess = distribute(customer,map.get(city),loginMobileList,orgPO);
                if(!isSuccess){
                    return;
                }
            }
        }
    }



    /**
     * 获取机构下当前客户城市门店员工列表
     * @param customer CustomerPO
     * @param map Map<String,List<OrgEmployeePO> city(key)->List<OrgEmployeePO>(value:员工集合)
     */
    private void getOrgCityEmployeeList(CustomerPO customer,Map<String,List<OrgEmployeePO>> map){
        String city = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CITY,customer.getOrgId().toString());//判断机构需求城市
        if(!JudgeUtil.in(city,CrmConstant.ALL_CITY)){ // 如果机构需求客户城市是全国,则客户城市变成全国
            city = customer.getCity();
        }
        // 获取该机构下 当前城市的所有门店下的员工
        List<OrgEmployeePO> employeeList = map.get(city);
        if(ListUtil.isEmpty(employeeList)){
            List<ShopPO> shopList = shopService.getShopByOrgId(customer.getOrgId(),city,CrmConstant.Shop.Type.QIAN); //修改: 原来的自动分配，只分配前端客户
            if(ListUtil.isEmpty(shopList)) {
                LOG.info("机构-{}，客户-{}，城市-{},没有配置门店 或者 门店已休息",customer.getOrgId(),customer.getMobile(),customer.getCity());
                return;
            }
            employeeList = new ArrayList<>();
            for(ShopPO shop : shopList){
                //查询门店下所有员工
                employeeList.addAll(employeeService.getAllEmployee(customer.getOrgId(),shop.getId(),null));
            }
            map.put(city,employeeList);
        }
    }





    @Transactional()
    private Boolean distribute(CustomerPO customer,List<OrgEmployeePO> employeeList,Set<String> loginMobileList,OrgPO orgPO){
        if(ListUtil.isEmpty(employeeList)){
            LOG.info("机构:{},没有可分配的员工",customer.getOrgId());
            return false;
        }
        Iterator<OrgEmployeePO> it = employeeList.iterator();
        while (it.hasNext()){
            OrgEmployeePO employee = it.next();
            if(!loginMobileList.contains(employee.getPhone())){
                if(LOG.isDebugEnabled())
                    LOG.info("员工-{}未登陆系统",employee.getPhone());
                it.remove();
                continue;
            }
            if(CrmConstant.Employee.Status.NO == employee.getStatus().byteValue()){
                LOG.info("分配客户：员工-{},是否在职-{}【已离职】",employee.getStatus());
                it.remove();
                continue;
            }
            if(employee.getCount().intValue() <=0){
                LOG.info("分配客户：员工-{},每日限量-{}【每日限量0】",employee.getCount());
                it.remove();
                continue;
            }
            if(CrmConstant.Employee.Receive.NO == employee.getReceive().byteValue()){
                LOG.info("分配客户：员工-{},接受状态-{}【不接受新客户】",employee.getReceive());
                it.remove();
                continue;
            }
            String key = CrmConstant.Config.Distribute.DIS_EMPLOYEE+employee.getPhone();
            if(stringRedisTemplate.opsForValue().getOperations().hasKey(key)){
                employee.setDistributeCount(Integer.valueOf(stringRedisTemplate.opsForValue().get(key)));
            }else{
                Integer count = customerService.getEmployeeDistributeCount(customer.getOrgId(),employee.getId(),DateUtil.cumputeStartDate(DateUtil.getCurrentDate(DateUtil.yyyymmdd)));
                stringRedisTemplate.opsForValue().set(key,count.toString(),1, TimeUnit.HOURS);
                employee.setDistributeCount(count);
            }
            if(employee.getCount() <=employee.getDistributeCount() ){
                it.remove();
            }
        }
        if(ListUtil.isEmpty(employeeList)){
            LOG.info("机构:{},没有可分配的员工",customer.getOrgId());
            return false;
        }
        Collections.sort(employeeList,(o1,o2)->{return o1.getDistributeCount()-o2.getDistributeCount();});
        OrgEmployeePO employee = employeeList.get(0);
        customer.setShopId(employee.getShopId());
        customer.setTeamId(employee.getTeamId());
        customer.setEmployeeId(employee.getId());
        customer.setDistributeDate(new Date());
        customer.setProgress(CrmConstant.Customer.Progress.IS_DIS);
        customer.setFirstEmployee(employee.getId());
        customer.setUpdateBy("分配");
        customerService.updateCustomer(customer);
        customerService.addCustomerDistributeRec(customer.getId(),employee.getId(),CrmConstant.Customer.DisStatus.NEW,"distribute");

        LOG.info("客户：{}-{}，分配给了：{}-{}-{}",customer.getName(),customer.getMobile(),employee.getName(),employee.getPhone(),employee.getDistributeCount());

        String employeeMsg = String.format(SmsApi.MESSAGE_TEMPLATE_DIS,orgPO.getNickname(),customer.getName());
        new WDSms().sendMessage(employee.getPhone(),employeeMsg,CrmConstant.CreateBy.DIS);

        String key = CrmConstant.Config.Distribute.DIS_EMPLOYEE+employee.getPhone();
        Integer count = employee.getDistributeCount()+1;
        stringRedisTemplate.opsForValue().set(key,count.toString(),2, TimeUnit.HOURS);

        String message = String.format(SmsApi.MSG_TEMPLATE_ADMIN,customer.getName());
        employeeMsgService.addMsg(new EmployeeMsgPO(employee.getId(),employee.getPhone(),message,CrmConstant.Employee.Msg.Status.NEW));

        return true;
    }




//    public static void main(String[] args){
////        CustomerPO po = new CustomerPO();
////        po.setName("张先森分发23");
////        po.setMobile("13832965123");
////        po.setChannel(19l);
////        po.setMedia("微信朋友圈");
////        po.setCity("上海市");
////        po.setAge(33);
////        po.setSex(Byte.valueOf("1"));
////        po.setNeed("300000");
////        po.setField1("有");
////        po.setField2("有");
////        po.setField3("有");
////        po.setField4("有");
////        po.setField5("有");
////        po.setField6("有");
////        po.setField7("有");
////        po.setField8("有");
////        po.setField9("有");
////        po.setField10("有");
////        po.setField11("无");
////        po.setRemark("有上海分期房，有三张信用卡11111");
////
////        String key = "c1b0aaf7281e4365a13986a1dde7e3cd";
////        System.out.println(DESUtil.encrypt(key,po.toString()));
//
//
//
//
//    }

}
