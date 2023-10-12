package com.crm.service.org.dao;


import com.crm.common.PageBO;
import com.crm.service.org.model.TeamPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

@Component
@Mapper
public interface TeamPOMapper {

    List<TeamPO> selectTeamByPage(PageBO<TeamPO> pageBO);

    Integer selectTeamTotalCountByPage(PageBO<TeamPO> pageBO);

    TeamPO selectTeamById(Long id);

    List<TeamPO> selectAllTeam(TeamPO teamPO);

    TeamPO selectTeam(@Param("orgId") Long orgId, @Param("shopId") Long shopId, @Param("teamName") String teamName);

    int delTeamById(Long id);

    int insertTeam(TeamPO team);

    int updateTeam(TeamPO team);

}