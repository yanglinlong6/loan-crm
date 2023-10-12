package com.daofen.crm.service.counselor.dao;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 机构顾问
 */
@Component
@Mapper
public interface CounselorMapper {


    List<CompanyCounselorBO> selectCounselorPage(PageVO<CompanyCounselorBO> pageVO);

    int selectCounselorPageCount(PageVO<CompanyCounselorBO> pageVO);

    CompanyCounselorPO selectCounselorById(Long id);

    CompanyCounselorPO selectCounselorMobile(String mobile);

    List<CompanyCounselorPO> selectCounselorByCompanyId(Long companyId);

    List<CompanyCounselorPO> selectCounselorByShopId(Long shopId);

    List<CompanyCounselorPO> selectCounselorByTeamId(Long teamId);

    int insertCounselor(CompanyCounselorPO record);

    int updateCounselor(CompanyCounselorPO record);

    void deleteCounselor(Long id);
    
    CompanyCounselorPO selectAdmin(Long id);

}