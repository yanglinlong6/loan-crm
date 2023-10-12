package com.daofen.crm.service.counselor;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 机构顾问接口
 */
public interface CounselorService {


    void getCounselorPage(PageVO<CompanyCounselorBO> pageVO);

    /**
     * 获取机构顾问对象
     * @param mobile 手机号码
     * @return CompanyCounselorBO
     */
    CompanyCounselorBO getCounselorByMobile(String mobile);

    List<CompanyCounselorPO> getCounselorByCompanyId(Long companyId);


    List<CompanyCounselorPO> getCounselorByShopId(Long shopId);
    /**
     * 获取团队顾问列表
     * @param teamId 团队id
     * @return  List<CompanyCounselorPO>
     */
    List<CompanyCounselorPO> getTeamCounselorByTeamId(Long teamId);

    /**
     * 新增顾问
     * @param counselor CompanyCounselorBO
     */
    void addCounselor(CompanyCounselorBO counselor);

    /**
     * 更新修改顾问西悉尼
     * @param counselor CompanyCounselorBO
     */
    void updateCounselor(CompanyCounselorBO counselor);

    @Transactional
    void delCounselor(Long id);
    
    CompanyCounselorPO selectAdmin(Long id);

}
