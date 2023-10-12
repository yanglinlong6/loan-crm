package com.daofen.crm.service.company.dao;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.company.model.TeamBO;
import com.daofen.crm.service.company.model.TeamPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface TeamMapper {

    List<TeamBO> selectTeamPage(PageVO<TeamBO> pageVO);

    int selectTeamPageCount(PageVO<TeamBO> pageVO);

    int deleteTeam(Long id);

    int insertTeam(TeamPO record);

    TeamPO selectTeamById(Long id);

    int updateTeam(TeamPO record);

    /**
     * 公司id和门店id不能同时为空
     * @param companyId 公司id
     * @param shopId 门店id
     * @return  List<TeamPO>
     */
    List<TeamPO> selectCompanyShopTeamList(@Param("companyId") Long companyId, @Param("shopId")Long shopId);

}