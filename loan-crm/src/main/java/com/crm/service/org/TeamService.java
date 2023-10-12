package com.crm.service.org;

import com.crm.common.PageBO;
import com.crm.service.org.model.TeamPO;

import java.util.List;

public interface TeamService {

    void getTeamPage(PageBO<TeamPO> pageBO);

    void addTeam(TeamPO teamPO);

    void updateTeam(TeamPO teamPO);

    void delTeam(Long id);

    /**
     * 根据团队id，获取团队信息
     * @param id 团队id
     * @return TeamPO
     */
    TeamPO getTeam(Long id);

    List<TeamPO> getAllTeam(TeamPO teamPO);

}
