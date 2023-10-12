package com.daofen.crm.service.company;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.dao.TeamMapper;
import com.daofen.crm.service.company.model.TeamBO;
import com.daofen.crm.service.company.model.TeamPO;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    private TeamMapper teamMapper;

    @Autowired
    private CompanyService companyService;

    @Override
    public void getTeamPage(PageVO<TeamBO> pageVO) {
        CompanyCounselorBO account = LoginUtil.getLoginUser();
        if(null == pageVO.getParam()){
            TeamBO teamBO = new TeamBO();
            pageVO.setParam(teamBO);
        }
        if(CrmConstant.Company.Type.PARENT == account.getCompany().getType().byteValue()){
            pageVO.getParam().setCompanyIds(companyService.getAllCompanyIds(account.getCompanyId()));
        }else{
            pageVO.getParam().setCompanyIds(account.getCompanyId().toString());
            pageVO.getParam().setShopId(account.getShopId());
        }
        pageVO.setData(teamMapper.selectTeamPage(pageVO));
        pageVO.setTotalCount(teamMapper.selectTeamPageCount(pageVO));
    }

    @Override
    public void addTeam(TeamPO teamPO) {
        if(null == teamPO.getCompanyId())
            teamPO.setCompanyId(LoginUtil.getLoginUser().getCompanyId());
        if(null == teamPO.getShopId())
            teamPO.setShopId(LoginUtil.getLoginUser().getShopId());
        teamPO.setUpdateBy(LoginUtil.getLoginUser().getId().toString());
        teamPO.setUpdateDate(new Date());
        teamMapper.insertTeam(teamPO);
    }

    @Override
    public void saveTeam(TeamPO teamPO) {
        if(null == teamPO.getCompanyId())
            teamPO.setCompanyId(LoginUtil.getLoginUser().getCompanyId());
        if(null == teamPO.getShopId())
            teamPO.setShopId(LoginUtil.getLoginUser().getShopId());
        teamPO.setUpdateBy(LoginUtil.getLoginUser().getId().toString());
        teamPO.setUpdateDate(new Date());
        teamMapper.updateTeam(teamPO);
    }

    @Override
    public void delTeam(Long id) {
        teamMapper.deleteTeam(id);
    }

    @Override
    public List<TeamPO> getShopTeam(Long companyId, Long shopId) {
        return teamMapper.selectCompanyShopTeamList(companyId,shopId);
    }
}
