package com.daofen.crm.service.company;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.company.model.TeamBO;
import com.daofen.crm.service.company.model.TeamPO;

import java.util.List;

public interface TeamService {

    /**
     * 团队模块，分页
     * @param pageVO PageVO<TeamPO>
     */
    void getTeamPage(PageVO<TeamBO> pageVO);

    /**
     * 新增团队
     * @param teamPO TeamPO
     */
    void addTeam(TeamPO teamPO);

    /**
     * 保存团队信息
     * @param teamPO TeamPO
     */
    void saveTeam(TeamPO teamPO);

    void delTeam(Long id);

    /**
     * 查询机构下的门店的所有团队列表
     * @param companyId 机构id
     * @param shopId 门店id
     * @return List<TeamPO>
     */
    List<TeamPO> getShopTeam(Long companyId,Long shopId);
}
