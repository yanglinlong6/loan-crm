package com.crm.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.service.label.LabelService;
import com.crm.service.label.model.LabelPO;
import com.crm.service.role.RoleService;
import com.crm.service.role.model.RoleBO;
import com.crm.service.statistic.dao.HomeStatisticMapper;
import com.crm.service.statistic.model.CustomerLabelBO;
import com.crm.service.statistic.model.CustomerStateBO;
import com.crm.service.statistic.model.HomeIncomeSortBO;
import com.crm.service.statistic.model.HomeStatisticBO;
import com.crm.util.DateUtil;
import com.crm.util.JudgeUtil;
import com.crm.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HomeStatisticServiceImpl implements HomeStatisticService {


    @Autowired
    HomeStatisticMapper homeStatisticMapper;

    @Override
    public List<HomeStatisticBO> getHomeStatistic() {
        HomeStatisticBO query = new HomeStatisticBO();

        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();

        RoleBO role = employee.getRole();
        if(CrmConstant.Role.Type.SHOP == role.getType())
            query.setShopId(employee.getShopId());
        if(CrmConstant.Role.Type.TEAM  == role.getType())
            query.setTeamId(employee.getTeamId());
        if(role.getType() >= CrmConstant.Role.Type.COMMON)
            query.setEmployeeId(employee.getId());
        query.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        query.setStartDate(DateUtil.cumputeStartDate(null));
        query.setEndDate(DateUtil.computeEndDate(null));

        HomeStatisticBO day = new HomeStatisticBO();
        day.setName("今日业绩");
        day.setNewCount(homeStatisticMapper.selectNewCustomerCount(query));//新分配数
        day.setHelpCount(homeStatisticMapper.selectHelpCustomerCount(query));// 协助数量
        day.setAgainCount(homeStatisticMapper.selectAgainCustomerCount(query)); // 再分配数量
        day.setContractCount(homeStatisticMapper.selectContractCount(query));
        day.setCallRate(day.computeRate(day.getNewCount(),homeStatisticMapper.selectCallCount(query)));//接通率
        day.setFitRate(day.computeRate(day.getNewCount(),homeStatisticMapper.selectFitCount(query)));// 有效率
        day.setDepositAmount(homeStatisticMapper.selectDepositAmount(query));// 诚意金 改成 合同金额
        day.setImportCount(homeStatisticMapper.selectImportCount(query)); // 进件数量
        day.setIncomeAmount(homeStatisticMapper.selectIncomeAmount(query)); //已收金额
        day.setShengyu(day.getDepositAmount()-day.getIncomeAmount());


        HomeStatisticBO week = new HomeStatisticBO();
        query.setStartDate(DateUtil.computeWeekDay(new Date(),2,DateUtil.yyyymmdd)+CrmConstant.Date.START);
        query.setEndDate(null);
        week.setName("本周业绩");
        week.setNewCount(homeStatisticMapper.selectNewCustomerCount(query));
        week.setHelpCount(homeStatisticMapper.selectHelpCustomerCount(query));
        week.setAgainCount(homeStatisticMapper.selectAgainCustomerCount(query));
        week.setContractCount(homeStatisticMapper.selectContractCount(query));
        week.setCallRate(day.computeRate(day.getNewCount(),homeStatisticMapper.selectCallCount(query)));//接通率
        week.setFitRate(day.computeRate(day.getNewCount(),homeStatisticMapper.selectFitCount(query)));// 有效率
        week.setDepositAmount(homeStatisticMapper.selectDepositAmount(query));
        week.setImportCount(homeStatisticMapper.selectImportCount(query));
        week.setIncomeAmount(homeStatisticMapper.selectIncomeAmount(query));
        week.setShengyu(week.getDepositAmount()-week.getIncomeAmount());

        HomeStatisticBO month = new HomeStatisticBO();
        query.setStartDate(DateUtil.computeMonthDay(new Date(),1,DateUtil.yyyymmdd)+CrmConstant.Date.START);
        month.setName("本月业绩");
        month.setNewCount(homeStatisticMapper.selectNewCustomerCount(query));
        month.setHelpCount(homeStatisticMapper.selectHelpCustomerCount(query));
        month.setAgainCount(homeStatisticMapper.selectAgainCustomerCount(query));
        month.setContractCount(homeStatisticMapper.selectContractCount(query));
        month.setCallRate(day.computeRate(day.getNewCount(),homeStatisticMapper.selectCallCount(query)));//接通率
        month.setFitRate(day.computeRate(day.getNewCount(),homeStatisticMapper.selectFitCount(query)));// 有效率
        month.setDepositAmount(homeStatisticMapper.selectDepositAmount(query));
        month.setImportCount(homeStatisticMapper.selectImportCount(query));
        month.setIncomeAmount(homeStatisticMapper.selectIncomeAmount(query));
        Double shengyu = month.getDepositAmount()-month.getIncomeAmount();
        month.setShengyu(shengyu);

        List<HomeStatisticBO> list = new ArrayList<>();
        list.add(day);
        list.add(week);
        list.add(month);

        return list;
    }

    @Autowired
    EmployeeService employeeService;

    @Autowired
    RoleService roleService;

    @Override
    public List<HomeIncomeSortBO> getIncomeSort(HomeIncomeSortBO bo) {
        if(null == bo){
            bo = new HomeIncomeSortBO();
        }
        String startDate = DateUtil.cumputeStartDate(bo.getStartDate());
        String endDate = DateUtil.computeEndDate(bo.getEndDate());
        Long orgId = LoginUtil.getLoginEmployee().getOrgId();
        List<OrgEmployeePO> employeeList = employeeService.getAllEmployee(orgId,null,null);
        if(ListUtil.isEmpty(employeeList))
            return null;
        List<HomeIncomeSortBO> data = new ArrayList<>();
        for(OrgEmployeePO po : employeeList){
            if(JudgeUtil.in(po.getRoleId().byteValue(),CrmConstant.Role.Type.ADMIN)){
                continue;
            }
            RoleBO role = roleService.getRoleBO(po.getRoleId());
            if(null != role && role.getType() == CrmConstant.Role.Type.ADMIN){
                continue;
            }
            HomeIncomeSortBO incomeSortBO = new HomeIncomeSortBO(orgId,po.getId(),po.getName(), startDate,endDate);
            incomeSortBO.setNewCount(homeStatisticMapper.selectEmployeeCustomerCount(incomeSortBO));
            incomeSortBO.setContractCount(homeStatisticMapper.selectEmployeeCustomerContractCount(incomeSortBO));
            incomeSortBO.computeContarctRate();
            incomeSortBO.setIncomeAmount(homeStatisticMapper.selectEmployeeNewCustomerIncomeAmount(incomeSortBO));

            incomeSortBO.setStartDate(null);
            incomeSortBO.setEndDate(null);
            incomeSortBO.setEmployeeId(null);
            incomeSortBO.setOrgId(null);

            data.add(incomeSortBO);
        }
        if(ListUtil.isEmpty(data))
            return data;

        data.sort((o1,o2) ->{
            return -o1.getIncomeAmount().compareTo(o2.getIncomeAmount());
        });
        return data;
    }

    @Autowired
    CacheConfigService cacheConfigService;

    @Override
    public List<CustomerStateBO> getProcessStatistic(CustomerStateBO bo) {
        if(null == bo){
            bo = new CustomerStateBO();
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        bo.setStartDate(DateUtil.cumputeStartDate(bo.getStartDate()));
        bo.setEndDate(DateUtil.computeEndDate(bo.getEndDate()));
        bo.setOrgId(employee.getOrgId());

        JSONArray statusArray = cacheConfigService.parseCustomerProcess(cacheConfigService.getCacheConfigValue(CrmConstant.Config.CUSTOMER_STATUS_KEY,bo.getOrgId().toString()));
        if(null == statusArray || statusArray.isEmpty()){
            return null;
        }
        RoleBO role = employee.getRole();
        if(null != role){
            switch (role.getType().byteValue()){
                case CrmConstant.Role.Type.SHOP :bo.setShopId(employee.getShopId());break;
                case CrmConstant.Role.Type.TEAM : bo.setTeamId(employee.getTeamId());break;
                case CrmConstant.Role.Type.COMMON : bo.setEmployeeId(employee.getId());break;
            }
        }
        List<CustomerStateBO> data = new ArrayList<>();
        Integer totalCount = homeStatisticMapper.selectCustomerCount(bo);
        for(int index=0;index<statusArray.size();index++){
            CustomerStateBO customerState = new CustomerStateBO();
            JSONObject status = statusArray.getJSONObject(index);
            customerState.setProcessName(status.getString("desc"));

            if(null == totalCount || totalCount <= 0){
                customerState.setCounts(0);
                customerState.setRate("(0)0.00%");
                data.add(customerState);
                continue;
            }
            bo.setProcess(status.getByte("status"));
            Integer processCount = homeStatisticMapper.selectCustomerCount(bo);
            if(null == processCount || processCount <= 0){
                customerState.setCounts(0);
                customerState.setRate("(0)0.00%");
            }else{
                customerState.setCounts(processCount);
                String rate = BigDecimal.valueOf(processCount).divide(BigDecimal.valueOf(totalCount),BigDecimal.ROUND_HALF_UP,2).multiply(BigDecimal.valueOf(100)).doubleValue()+"%";
                customerState.setRate("("+processCount+")"+rate);
            }
            data.add(customerState);
        }
        return data;
    }

    @Autowired
    LabelService labelService;

    @Override
    public List<CustomerLabelBO> getLabelStatistic(CustomerLabelBO bo) {
        if(null == bo){
            bo = new CustomerLabelBO();
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        bo.setStartDate(DateUtil.cumputeStartDate(bo.getStartDate()));
        bo.setEndDate(DateUtil.computeEndDate(bo.getEndDate()));
        bo.setOrgId(employee.getOrgId());
        RoleBO role = employee.getRole();
        if(null != role){
            switch (role.getType().byteValue()){
                case CrmConstant.Role.Type.SHOP :bo.setShopId(employee.getShopId());break;
                case CrmConstant.Role.Type.TEAM : bo.setTeamId(employee.getTeamId());break;
                case CrmConstant.Role.Type.COMMON : bo.setEmployeeId(employee.getId());break;
            }
        }

        List<LabelPO> list = labelService.selectAllLabel(bo.getOrgId());
        if(ListUtil.isEmpty(list)){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"没有设置标签");
        }
        Integer totalCount = homeStatisticMapper.selectCustomerLabelCount(bo);
        totalCount = (totalCount == null ? 0 : totalCount);
        Integer labelCount = 0;
        List<CustomerLabelBO> data = new ArrayList<>();
        for(LabelPO label : list){
            CustomerLabelBO customerLabel = new CustomerLabelBO();
            bo.setLabel(label.getName());
            customerLabel.setLabel(label.getName());
            if(totalCount.intValue() <= 0){
                customerLabel.setCount(0);
                customerLabel.setRate("(0)0.00%");
                data.add(customerLabel);
                continue;
            }
            Integer count = homeStatisticMapper.selectCustomerLabelCount(bo);
            if(null == count || count <= 0){
                customerLabel.setCount(0);
                customerLabel.setRate("(0)0.00%");
            }else{
                labelCount += count;
                customerLabel.setCount(count);
                String rate = BigDecimal.valueOf(count).divide(BigDecimal.valueOf(totalCount),BigDecimal.ROUND_HALF_UP,2).multiply(BigDecimal.valueOf(100)).doubleValue()+"%";
                customerLabel.setRate("("+count+")"+rate);
            }
            data.add(customerLabel);
        }
        Integer cha = totalCount - labelCount;
        if(cha > 0){ // 如果总数量-已标记总和 > 0  则表示客户没标记完,增加一个"未标记"标签
            CustomerLabelBO customerLabel = new CustomerLabelBO();
            customerLabel.setLabel("未标记");
            customerLabel.setCount(cha);
            String rate = BigDecimal.valueOf(cha).divide(BigDecimal.valueOf(totalCount),BigDecimal.ROUND_HALF_UP,2).multiply(BigDecimal.valueOf(100)).doubleValue()+"%";
            customerLabel.setRate("("+cha+")"+rate);
            data.add(customerLabel);
        }
        return data;
    }


}
