package com.crm.service.org;

import com.crm.common.CrmConstant;
import com.crm.common.CrmException;
import com.crm.common.PageBO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.org.dao.TeamPOMapper;
import com.crm.service.org.model.TeamPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamPOMapper teamPOMapper;

    @Override
    public void getTeamPage(PageBO<TeamPO> page) {
        if(null == page){
            return;
        }
        if(page.getParamObject()  == null){
            TeamPO team = new TeamPO();
            team.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
            page.setParamObject(team);
        }else
            page.getParamObject().setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        page.setDataList(teamPOMapper.selectTeamByPage(page));
        int totalCount = teamPOMapper.selectTeamTotalCountByPage(page);
        page.setTotalCount(totalCount);
        if(totalCount == 0){
            page.setPageCount(0);
        }else if(totalCount%page.getSize() == 0){
            page.setPageCount(totalCount/page.getSize());
        }else
            page.setPageCount(totalCount/page.getSize()+1);
    }

    @Override
    public void addTeam(TeamPO teamPO) {
        if(null == teamPO || null == teamPO.getShopId()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"新增团队信息错误!");
        }
        teamPO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        TeamPO oldTeamPO = teamPOMapper.selectTeam(teamPO.getOrgId(),teamPO.getShopId(),teamPO.getName());
        if(null != oldTeamPO){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"团队名称已经存在!");
        }
        teamPO.setCreateBy(LoginUtil.getLoginEmployee().getName());
        teamPO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        teamPOMapper.insertTeam(teamPO);
    }

    @Override
    public void updateTeam(TeamPO teamPO) {
        if(null == teamPO || null == teamPO.getShopId()){
            throw new CrmException(CrmConstant.ResultCode.FAIL,"更新团队信息错误!");
        }
        teamPO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        TeamPO oldTeamPO = teamPOMapper.selectTeam(teamPO.getOrgId(),teamPO.getShopId(),teamPO.getName());
        teamPO.setCreateBy(LoginUtil.getLoginEmployee().getName());
        teamPO.setUpdateBy(LoginUtil.getLoginEmployee().getName());
        if(null == oldTeamPO || teamPO.getId() == oldTeamPO.getId()){
            teamPOMapper.updateTeam(teamPO);
            return;
        }
        throw new CrmException(CrmConstant.ResultCode.FAIL,"更新团队:团队名称已存在!");
    }

    @Override
    public void delTeam(Long id) {
        teamPOMapper.delTeamById(id);
    }

    @Override
    public TeamPO getTeam(Long id) {
        if(null == id)
            return null;
        return teamPOMapper.selectTeamById(id);
    }

    @Override
    public List<TeamPO> getAllTeam(TeamPO teamPO) {
        return teamPOMapper.selectAllTeam(teamPO);
    }
}
