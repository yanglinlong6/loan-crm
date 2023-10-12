package com.crm.service.customer;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.behind.dao.CustomerBehindRemarkMapper;
import com.crm.service.behind.model.CustomerBehindRemarkPO;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.customer.dao.*;
import com.crm.service.customer.model.*;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CustomerServiceImpl implements CustomerService{

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    CustomerFieldMapper customerFieldMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CustomerRemarkMapper customerRemarkMapper;

    @Autowired
    CustomerCommentMapper customerCommentMapper;

    @Autowired
    CustomerImportMapper customerImportMapper;

    @Autowired
    CustomerContractMapper customerContractMapper;

    @Autowired
    ContractProductPOMapper contractProductPOMapper;

    @Autowired
    CustomerBehindRemarkMapper customerBehindRemarkMapper;


    @Autowired
    EmployeeService employeeService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String ORG_CUSTOMER_FIELD = "crm_org_customer_field_";

    @Override
    public List<CustomerFieldPO> getAllField(Long orgId) {
        if(null == orgId || orgId <= 0){
            return null;
        }
        List<CustomerFieldPO> list;
        String key = ORG_CUSTOMER_FIELD+orgId;
        String object = redisTemplate.opsForValue().get(key);
        if(StringUtils.isBlank(object)){
            list = customerFieldMapper.selectAllField(orgId);
            if(!ListUtil.isEmpty(list))
                redisTemplate.opsForValue().set(key,JSONUtil.toJSONString(list),1, TimeUnit.HOURS);
        }else{
            list = JSONUtil.toJavaBeanList(object,CustomerFieldPO.class);
        }
        if(ListUtil.isEmpty(list))
            return list;
        for(CustomerFieldPO po : list){
            String value = po.getValue();
            if(StringUtils.isBlank(value))
                continue;
            String[] values = po.getValue().replaceAll("\\[","").replaceAll("\\]","").split(",");
            if(values.length > 1){
                po.setType(Byte.valueOf("2"));
            }
        }
        return list;
    }


    @Override
    public void getCustomerPage(PageBO<CustomerBO> pageBO) {
        CustomerBO customerBO = setCustomerParam(pageBO.getParamObject());
        // 计算日期
        if(StringUtils.isNotBlank(customerBO.getStartDate()))
            customerBO.setStartDate(DateUtil.cumputeStartDate(pageBO.getParamObject().getStartDate()));
        else
            customerBO.setStartDate(DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd)+CrmConstant.Date.START);
        if(StringUtils.isNotBlank(customerBO.getEndDate()))
            customerBO.setEndDate(DateUtil.computeEndDate(pageBO.getParamObject().getEndDate()));

        if (customerBO.getQueryThirdParty()){
            customerBO.setOldOrgId(LoginUtil.getLoginEmployee().getOrgId());
        }else{
            customerBO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        }
        if(null != customerBO.getChannelId() && customerBO.getChannelId().intValue() > 0){
            customerBO.setChannel(customerBO.getChannelId());
        }
        if(StringUtils.isNotBlank(customerBO.getLabel())){
            customerBO.setLabels(Arrays.asList(customerBO.getLabel().split(",")));
        }
        pageBO.setParamObject(customerBO);


        List<CustomerBO> dataList = customerMapper.selectCustomerPage(pageBO);

        if(ListUtil.isEmpty(dataList)){
            pageBO.setPageCount(0);
            pageBO.setTotalCount(0);
            return;
        }
        for(CustomerBO bo : dataList){

            // 设置条件备注
            String remark = bo.getRemark();
            if(StringUtils.isNotBlank(remark)){
                remark = remark.replace(",",";").replace("；",";");
                bo.setRemark(remark);
            }

            //计算多少天未跟进
            bo.setDays(DateUtil.computeDayDifference(bo.getRemarkDate(),new Date())+"天");

            // 设置跟进记录
            bo.setRemarkList(addBehindRemark(customerRemarkMapper.selectCustomerRemarkAll(bo.getId()),bo.getId()));

            // 设置签约信息,以及签约产品
            List<CustomerContractBO> contractList = customerContractMapper.selectCustomerContractByCustomerId(bo.getId());
            if(!ListUtil.isEmpty(contractList)){
                List<ContractProductPO> products = new ArrayList<>();
                for(CustomerContractBO contract : contractList){
                    products.addAll(contractProductPOMapper.selectContractProduct(bo.getOrgId(),contract.getId()));
                }
                bo.setProductList(products);
            }

            // 跟进资料信息
            List<CustomerZiliaoPO> ziliaoList = customerMapper.selectCustomerZiliaoList(bo.getId(),bo.getOrgId(),null);
            bo.setCustomerZiliao(ziliaoList);
            StringBuffer otherFile = new StringBuffer();
            if(!ListUtil.isEmpty(ziliaoList)){
                for(int i=0;i<ziliaoList.size();i++){
                    CustomerZiliaoPO ziliao = ziliaoList.get(i);
                    if(i == 0) otherFile.append(ziliao.getImage());
                    else otherFile.append(",").append(ziliao.getImage());
                }
            }
            // 其他跟进资料图片文件
            bo.setOtherFile(otherFile.toString());

            // 设置主管点评
            CustomerCommentPO comment = customerCommentMapper.selectCustomerCommentByCustomerId(bo.getId());
            if(null != comment){
                bo.setComment(comment.getContent());
            }
            if(CrmConstant.Customer.Again.TRUE == bo.getAgainDistribute()){
                bo.setEmployee(employeeService.getEmployeeById(bo.getAgainEmployeeId()));
            }else{
                bo.setEmployee(employeeService.getEmployeeById(bo.getEmployeeId()));
            }
            if(1 != pageBO.getParamObject().getAlls()){
                continue;
            }
//            if(LoginUtil.getLoginEmployee().getRoleId().byteValue() != CrmConstant.Role.Type.ADMIN){
//                String mobile = bo.getMobile();
//                String start = mobile.substring(0,3);
//                String end = mobile.substring(8,mobile.length());
//                bo.setMobile(start+"****"+end);
//            }
        }
        // 设置分页
        pageBO.setDataList(dataList);
        int totalCount = customerMapper.selectCustomerPageCount(pageBO);
        pageBO.setTotalCount(totalCount);
        if(totalCount == 0){
            pageBO.setPageCount(0);
        }else if(totalCount%pageBO.getSize() == 0){
            pageBO.setPageCount(totalCount/pageBO.getSize());
        }else
            pageBO.setPageCount(totalCount/pageBO.getSize()+1);

        pageBO.setParamObject(null);
    }

    private List<CustomerRemarkPO> addBehindRemark(List<CustomerRemarkPO> remarkList,Long customerId){
        if(ListUtil.isEmpty(remarkList)){
            remarkList = new ArrayList<>();
        }
        List<CustomerBehindRemarkPO> behindList = customerBehindRemarkMapper.selectAllCustomerBehindRemark(customerId);
        if(ListUtil.isEmpty(behindList)){
            return remarkList;
        }
        for(CustomerBehindRemarkPO po: behindList){
            CustomerRemarkPO newCustomerRemark = JSONUtil.toJavaBean(po.toString(),CustomerRemarkPO.class);
            remarkList.add(newCustomerRemark);
        }
        return remarkList;
    }

    @Override
    public void getImportCustomerPage(PageBO<CustomerBO> pageBO) {
        List<CustomerBO> list = customerMapper.selectImportCustomerPage(pageBO);
        if(ListUtil.isEmpty(list)){
            pageBO.setPageCount(0);
            pageBO.setTotalCount(0);
            return;
        }
        for(CustomerBO bo : list){
            List<CustomerContractBO> contractList = customerContractMapper.selectCustomerContractByCustomerId(bo.getId());
            BigDecimal deposit = new BigDecimal(0);
            Double surplus = 0d;
            Double income = 0d;
            StringBuffer contractCodeBuffer =new StringBuffer();
            if(ListUtil.isEmpty(contractList)){
                CustomerContractBO contract = new CustomerContractBO();
                contract.setIncomeCount(0);//进件次数
                contract.setDeposit(deposit); // 合同金额
                contract.setSurplusAmount(surplus);
                contract.setIncomeAmount(income);
                contract.setContractStatus("待收款");
                bo.setContractBO(contract);
                continue;
            }

            for(CustomerContractBO contract : contractList){
                deposit = deposit.add(contract.getDeposit()); // 计算当签客户的总合同金额
                contractCodeBuffer.append(contract.getContractCode()+","); // 多个合同拼接
                List<CustomerImportPO> importList = customerImportMapper.selectImportsByContract(contract.getId(),pageBO.getParamObject().getOrgId(),null,null,null);
                if(ListUtil.isEmpty(importList)){
                    continue;
                }
                for(CustomerImportPO importPO : importList){
                    income += importPO.getIncome().doubleValue();
                }
            }
            String contractCode = contractCodeBuffer.toString();

            CustomerContractBO contractBO = contractList.get(0);
            contractBO.setIncomeCount(contractList.size());
            contractBO.setDeposit(deposit);
            contractBO.setIncomeAmount(income);
            contractBO.setSurplusAmount(deposit.doubleValue() - income);
            contractBO.setContractCode(contractCode.endsWith(",")?contractCode.substring(0,contractCode.length()-1):contractCode);
            if(income == 0){
                contractBO.setContractStatus("待收款");
            }else if(deposit.doubleValue() == income){
                contractBO.setContractStatus("已完成");
            }else{
                contractBO.setContractStatus("收款中");
            }
            bo.setContractBO(contractBO);
        }

        pageBO.setDataList(list);
        int totalCount = customerMapper.selectImportCustomerPageCount(pageBO);
        pageBO.setTotalCount(totalCount);
        if(totalCount == 0){
            pageBO.setPageCount(0);
        }else if(totalCount%pageBO.getSize() == 0){
            pageBO.setPageCount(totalCount/pageBO.getSize());
        }else
            pageBO.setPageCount(totalCount/pageBO.getSize()+1);
    }

    @Override
    public List<CustomerBO> getMyAllCustomer(CustomerBO bo) {
        if(null == bo)
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询客户:缺少查询参数");
        if(StringUtils.isBlank(bo.getName()) && StringUtils.isBlank(bo.getMobile()))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"查询客户:缺少查询参数");
        bo.setOrgId(LoginUtil.getLoginEmployee().getOrgId());

        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        if(employeeBO.getRoleId().intValue() != 0 && !JudgeUtil.in(employeeBO.getRole().getType().byteValue(),CrmConstant.Role.Type.ADMIN,CrmConstant.Role.Type.SHOP,CrmConstant.Role.Type.TEAM)){
            bo.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        }
        if(StringUtils.isBlank(bo.getStartDate())){
            bo.setStartDate("2021-01-01");
        }
        List<CustomerBO> list = customerMapper.selectMyAllCustomer(bo);
        if(ListUtil.isEmpty(list)){
            return list;
        }
        for(CustomerBO customer : list){
            List<CustomerZiliaoPO> ziliaoList = customerMapper.selectCustomerZiliaoByCustomerId(customer.getId());
            if(ListUtil.isEmpty(ziliaoList)){
                continue;
            }
            StringBuffer otherFile = new StringBuffer();
            for(int i=0;i<ziliaoList.size();i++){
                CustomerZiliaoPO ziliao = ziliaoList.get(i);
                if(i == ziliaoList.size()-1){
                    otherFile.append(ziliao.getImage());
                }else{
                    otherFile.append(ziliao.getImage()).append(",");
                }
                customer.setOtherFile(otherFile.toString());
            }
        }
        return list;
    }

    @Override
    public CustomerPO getCustomerByMd5(Long orgId, String mobileMd5) {
        if(null == orgId || StringUtils.isBlank(mobileMd5))
            throw new CrmException(CrmConstant.ResultCode.FAIL,"机构id为空 或者 手机号码md5是空的");
        return customerMapper.selectCustomerByMd5(orgId,mobileMd5);
    }

    @Override
    public List<CustomerPO> getNewCustomerList(Long orgId) {
        return customerMapper.selectOrgNewCustomer(orgId);
    }

    @Override
    public CustomerPO getCustomerById(Long id) {
        if(null == id || id <= 0)
            return null;
        CustomerPO customerPO = customerMapper.selectCustomerById(id);
        if(null == customerPO)
            return customerPO;
        List<CustomerZiliaoPO> ziliaoList = customerMapper.selectCustomerZiliaoList(id,null,null);
        if(ListUtil.isEmpty(ziliaoList)){
            return customerPO;
        }
        StringBuffer otherFile = new StringBuffer();
        for(int i=0;i<ziliaoList.size();i++){
            CustomerZiliaoPO ziliao = ziliaoList.get(i);
            if(i == ziliaoList.size()-1){
                otherFile.append(ziliao.getImage());
            }else{
                otherFile.append(ziliao.getImage()).append(",");
            }
            customerPO.setOtherFile(otherFile.toString());
        }
        customerPO.setOtherFile(otherFile.toString());
        return customerPO;
    }

    @Override
    public int getChannelNewCustomerCount(Long orgId, Long channelId, String city, String media, String startDate) {
        Integer count = customerMapper.selectChannelNewCustomerCount(orgId,channelId,city,media,startDate);
        if(null == count)
            return 0;
        return count;
    }

    @Override
    public int getEmployeeDistributeCount(Long orgId, Long employeeId, String startDate) {
        Integer count = customerMapper.selectEmployeeDistributeCount(orgId,employeeId,CrmConstant.Customer.DisStatus.NEW,startDate);
        if(null == count)
            return 0;
        return count.intValue();
    }

    @Override
    public void addCustomer(CustomerPO po) {
        if(StringUtils.isBlank(po.getName())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"姓名必填");
        }
        if(StringUtils.isBlank(po.getMobile())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"电话必填");
        }
        if(StringUtils.isBlank(po.getCity())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"城市必填");
        }
        if(null == po.getSex()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"性别必填");
        }
        if(null == po.getAge()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"年龄必填");
        }
//        if(StringUtils.isBlank(po.getNeed())){
//            throw new CrmException(CrmConstant.ResultCode.FAIL,"需求必填");
//        }
        if(StringUtils.isBlank(po.getMedia())){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"媒体必须");
        }
        if(null == po.getChannel()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"渠道必须");
        }
        byte init = Byte.valueOf(CrmConstant.Customer.init);
        if(StringUtils.isBlank(po.getLevel()))
            po.setLevel(CrmConstant.Customer.init);
        if(null == po.getCall())
            po.setCall(init);
        if(null == po.getFit())
            po.setFit(init);
        if(null == po.getProgress())
            po.setProgress(init);
        po.setMobileMd5(MD5Util.getMd5String(po.getMobile()));
        OrgEmployeeBO employeeBO = LoginUtil.getLoginEmployee();
        if(null == employeeBO)
            po.setCreateBy("system");
        else
            po.setCreateBy(LoginUtil.getLoginEmployee().getName());
        po.setFirstEmployee(po.getEmployeeId());
        customerMapper.insertCustomer(po);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateCustomer(CustomerPO po) {
        if(null == po || null == po.getId())
            return;
        po.setUpdateDate(new Date());
        log.info("更新客户信息:{}",po.toString());
        customerMapper.updateCustomer(po);
        if(JudgeUtil.in(po.getThirdparty(),CrmConstant.Customer.ThirdParty.Y)){
            customerRemarkMapper.insertCustomerRemark(new CustomerRemarkPO(po.getId(),LoginUtil.getLoginEmployee().getId(),po.getThirdPartyRemark()));
        }
        // 更新客户的其他资料
        if(StringUtils.isNotBlank(po.getOtherFile())){
            String[] files = po.getOtherFile().split(",");
            OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
            for(String file : files){
                CustomerZiliaoPO ziliao = null;
                if(null == employee){
                    ziliao = new CustomerZiliaoPO(po.getId(),null,null,file);
                }else{
                    ziliao = new CustomerZiliaoPO(po.getId(),employee.getOrgId(),employee.getId(),file);
                }
                CustomerZiliaoPO oldZiliao = customerMapper.selectCustomerZiliaoByZiliao(po.getId(),file);
                if(null == oldZiliao){
                    customerMapper.insertCustomerZiliao(ziliao);
                }
            }
        }
    }

    @Autowired
    CacheConfigService cacheConfigService;

    @Transactional
    @Override
    public void updateCustomer(CustomerBO bo) {

        CustomerPO po = JSONUtil.toJavaBean(bo.toString(),CustomerPO.class);
        if(StringUtils.isNotBlank(bo.getRemark())){
            po.setRemarkDate(new Date());//设置最后跟进日期
        }
        if(( null != bo.getCustomerRemark() && StringUtils.isNotBlank(bo.getCustomerRemark().getRemark()))){
            po.setRemarkDate(new Date());//设置最后跟进日期
        }
        if(StringUtils.isBlank(bo.getIdcardBack())
                || StringUtils.isBlank(bo.getIdcardFront())
                || StringUtils.isBlank(bo.getCreditFile())
                || StringUtils.isBlank(bo.getAuthorizeFile())
                || StringUtils.isBlank(bo.getOtherFile())){
            po.setRemarkDate(new Date());//设置最后跟进日期
        }
        CustomerRemarkPO remark = bo.getCustomerRemark();

        CustomerPO oldCustomer = customerMapper.selectCustomerById(po.getId());
        // 判断客户是否跟进中
        if(null != remark){
            if(null != oldCustomer){
                String processStr = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,LoginUtil.getLoginEmployee().getOrgId().toString());
                Byte process = cacheConfigService.parseCustomerProcess(processStr,"跟进中");
                if(null != process && oldCustomer.getProgress() <= process){
                    po.setProgress(process);
                }
            }
        }

        // 判断客户是否有意向
        if(StringUtils.isNotBlank(po.getLabel()) && JudgeUtil.contain2(po.getLabel(),"3星","4星","5星")){
            String processStr = cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,LoginUtil.getLoginEmployee().getOrgId().toString());
            Byte process = cacheConfigService.parseCustomerProcess(processStr,"有意向");
            if(null != process && oldCustomer.getProgress() <= process){
                po.setProgress(process);
            }
        }

        updateCustomer(po);
        // 如果客户的跟进记录不是空的，则新增跟进记录
        if(null != remark){
            remark.setCustomerId(bo.getId());
            remark.setEmployeeId(LoginUtil.getLoginEmployee().getId());
            remark.setCreateBy(LoginUtil.getLoginEmployee().getName());
            customerRemarkMapper.insertCustomerRemark(remark);
        }
        if(null != bo.getEmployeeId())
            customerMapper.insertCustomerDistributeRec(bo.getId(),bo.getEmployeeId(),CrmConstant.Customer.DisStatus.AGEIN,LoginUtil.getLoginEmployee().getName());
        if(null != bo.getHelpEmployeeId())
            customerMapper.insertCustomerDistributeRec(bo.getId(),bo.getHelpEmployeeId(),CrmConstant.Customer.DisStatus.HELP,LoginUtil.getLoginEmployee().getName());
    }

    @Override
    public void delCustomer(Long id) {
        if(null == id || id <= 0){
            return;
        }
        customerMapper.deleteCustomer(id);
    }

    @Override
    public void updateCustomerByEmployee(OrgEmployeePO employee) {
        if(null == employee || null == employee.getId()){
            return;
        }
        customerMapper.updateCustomerShopTeamByEmployee(employee);
    }

    @Override
    public void addPublicPool(CustomerBO bo) {
        if(null == bo){
            throw new CrmException(CrmConstant.ResultCode.FAIL, "加入公共池失败：缺少客户信息");
        }
        if(null != bo.getId() && bo.getId().intValue() > 0){
            bo.setEmployeeId(0l);
            bo.setPublicPool(CrmConstant.Customer.PublicPool.Y);// 加入公共池
            updateCustomer(JSONUtil.toJavaBean(bo.toString(),CustomerPO.class));
        }
        if(null != bo.getIdList() && bo.getIdList().size() > 0){
            for(Long id : bo.getIdList()){
                CustomerPO customerPO = new CustomerPO();
                customerPO.setId(id);
                customerPO.setEmployeeId(0l);
                customerPO.setPublicPool(CrmConstant.Customer.PublicPool.Y);// 加入公共池
                updateCustomer(customerPO);
            }
        }
    }

    @Override
    public void addCustomerComment(CustomerCommentPO comment) {
        if(null == comment || null == comment.getCustomerId() || StringUtils.isBlank(comment.getContent()))
            throw new CrmException(CrmConstant.ResultCode.FAIL, "主管点评:缺少点评内容");
        comment.setEmployeeId(LoginUtil.getLoginEmployee().getId());
        comment.setCreateBy(LoginUtil.getLoginEmployee().getName());
        customerCommentMapper.insertCustomerComment(comment);
    }

    @Override
    public void addCustomerDistributeRec(Long customerId, Long employeeId, Byte status, String createBy) {
        customerMapper.insertCustomerDistributeRec(customerId,employeeId,status,createBy);
    }

}
