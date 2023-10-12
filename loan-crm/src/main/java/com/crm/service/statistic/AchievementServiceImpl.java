package com.crm.service.statistic;

import com.crm.common.CrmConstant;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.org.ShopService;
import com.crm.service.org.model.ShopPO;
import com.crm.service.statistic.dao.AchievementMapper;
import com.crm.service.statistic.model.AchievementBO;
import com.crm.util.ComputeRateUtil;
import com.crm.util.ListUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AchievementServiceImpl implements AchievementService {

    @Autowired
    AchievementMapper achievementMapper;

    @Autowired
    private CacheConfigService cacheConfigService;

    @Override
    public void getAchievementPersonalPage(PageBO<AchievementBO> page) {
        setParam(page);
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        if(CrmConstant.Role.Type.COMMON == employee.getRole().getType()){
            page.getParamObject().setEmployeeId(employee.getId());
        }
        List<AchievementBO> list = achievementMapper.selectPersonalPage(page);
        if(ListUtil.isEmpty(list))
            return;

        String city = cacheConfigService.getCacheConfigValue("city",employee.getOrgId().toString());
        for(AchievementBO achievement : list){
            achievement.setOrgId(employee.getOrgId());
            achievement.setEmployeeId(achievement.getId());
            achievement.setStartDate(page.getParamObject().getStartDate());
            achievement.setEndDate(page.getParamObject().getEndDate());
            achievement.setCity(city);

            achievement.setCounts(achievementMapper.selectNewCustomerCount(achievement)); // 新分配的客户数，不包含再分配和自建客户
            achievement.setContractCount(achievementMapper.selectContractCount(achievement)); // 签约数量
            achievement.setDepositAmount(achievementMapper.selectContractDepositAmount(achievement)); // 合同总金额
            achievement.setIncomeTotalAmount(achievementMapper.selectImportIncomeAmount(achievement));// 已创收总金额
            achievement.setImportCount(achievementMapper.selectImportCount(achievement)); // 进件数量
            achievement.setSurplusAmount(achievement.getDepositAmount()-achievement.getIncomeTotalAmount());// 未收金额
            achievement.setConsumeAmount(achievementMapper.selectConsumeAmount(achievement));// 投入成本
            achievement.setCallRate(ComputeRateUtil.computeRate(achievementMapper.selectCallCustomerCount(achievement),achievement.getCounts()));// 接通率
            achievement.setFitRate(ComputeRateUtil.computeRate(achievementMapper.selectFitCustomerCount(achievement),achievement.getCounts())); //有效率
            achievement.setRate(achievementMapper.selectContractCostRateAvg(achievement)); // 平均费率



        }
        page.setDataList(list);
        page.setTotalCount(achievementMapper.selectPersonalPageCount(page));

    }

    @Autowired
    ShopService shopService;
    @Override
    public void getAchievementTeamPage(PageBO<AchievementBO> page) {
        setParam(page);
        List<AchievementBO> list = achievementMapper.selectTeamPage(page);
        if(ListUtil.isEmpty(list))
            return;
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        for(AchievementBO achievement : list){

            ShopPO shop = shopService.getShop(achievement.getShopId());

            achievement.setOrgId(employee.getOrgId());
            achievement.setTeamId(achievement.getId());
            achievement.setShopName(null == shop ? null:shop.getName());
            achievement.setStartDate(page.getParamObject().getStartDate());
            achievement.setEndDate(page.getParamObject().getEndDate());
//            achievement.setProductTotalAmount(achievementMapper.selectImportCompletionAmount(achievement));

            achievement.setCounts(achievementMapper.selectNewCustomerCount(achievement)); // 新分配的客户数，不包含再分配和自建客户
            achievement.setContractCount(achievementMapper.selectContractCount(achievement));
            achievement.setDepositAmount(achievementMapper.selectContractDepositAmount(achievement));
            achievement.setIncomeTotalAmount(achievementMapper.selectImportIncomeAmount(achievement));
            achievement.setImportCount(achievementMapper.selectImportCount(achievement));
            achievement.setSurplusAmount(achievement.getDepositAmount()-achievement.getIncomeTotalAmount());// 未收金额
            achievement.setConsumeAmount(achievementMapper.selectConsumeAmount(achievement));// 投入成本
            achievement.setCallRate(ComputeRateUtil.computeRate(achievementMapper.selectCallCustomerCount(achievement),achievement.getCounts()));// 接通率
            achievement.setFitRate(ComputeRateUtil.computeRate(achievementMapper.selectFitCustomerCount(achievement),achievement.getCounts())); //有效率
            achievement.setRate(achievementMapper.selectContractCostRateAvg(achievement));
        }
        page.setDataList(list);
        page.setTotalCount(achievementMapper.selectTeamPageCount(page));

    }

    @Override
    public void getAchievementShopPage(PageBO<AchievementBO> page) {
        setParam(page);
        List<AchievementBO> list = achievementMapper.selectShopPage(page);
        if(ListUtil.isEmpty(list))
            return;
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        for(AchievementBO achievement : list){
            achievement.setOrgId(employee.getOrgId());
            achievement.setShopId(achievement.getId());
            achievement.setStartDate(page.getParamObject().getStartDate());
            achievement.setEndDate(page.getParamObject().getEndDate());

            achievement.setCounts(achievementMapper.selectNewCustomerCount(achievement)); // 新分配的客户数，不包含再分配和自建客户
            achievement.setContractCount(achievementMapper.selectContractCount(achievement));
            achievement.setDepositAmount(achievementMapper.selectContractDepositAmount(achievement));
            achievement.setImportCount(achievementMapper.selectImportCount(achievement));
            achievement.setIncomeTotalAmount(achievementMapper.selectImportIncomeAmount(achievement));
            achievement.setSurplusAmount(achievement.getDepositAmount()-achievement.getIncomeTotalAmount());// 未收金额
            achievement.setConsumeAmount(achievementMapper.selectConsumeAmount(achievement));// 投入成本
            achievement.setCallRate(ComputeRateUtil.computeRate(achievementMapper.selectCallCustomerCount(achievement),achievement.getCounts()));// 接通率
            achievement.setFitRate(ComputeRateUtil.computeRate(achievementMapper.selectFitCustomerCount(achievement),achievement.getCounts())); //有效率
            achievement.setRate(achievementMapper.selectContractCostRateAvg(achievement));
        }
        page.setDataList(list);
        page.setTotalCount(achievementMapper.selectShopPageCount(page));

    }
}
