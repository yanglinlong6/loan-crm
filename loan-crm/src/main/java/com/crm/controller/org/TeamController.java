package com.crm.controller.org;

import com.crm.common.PageBO;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.org.TeamService;
import com.crm.service.org.model.TeamPO;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 团队管理
 */
@RestController
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    private TeamService teamService;

    @PostMapping("/team/page")
    @ResponseBody
    public ResultVO page(@RequestBody() PageBO<TeamPO> pageBO){
        teamService.getTeamPage(pageBO);
        return ResultVO.success("团队分页列表成功",pageBO);
    }

    @PostMapping("/team/add")
    @ResponseBody
    public ResultVO add(@RequestBody() TeamPO teamPO){
        teamService.addTeam(teamPO);
        return ResultVO.success("新增团队成功",teamPO);
    }

    @PostMapping("/team/update")
    @ResponseBody
    public ResultVO update(@RequestBody() TeamPO teamPO){
        teamService.updateTeam(teamPO);
        return ResultVO.success("更新团队成功",teamPO);
    }

    @PostMapping("/team/del")
    @ResponseBody
    public ResultVO del(@RequestBody() TeamPO teamPO){
        teamService.delTeam(teamPO.getId());
        return ResultVO.success("删除团队成功",null);
    }

    @PostMapping("/team/all")
    @ResponseBody
    public ResultVO all(@RequestBody(required=false) TeamPO teamPO){
        if(null == teamPO)
            teamPO = new TeamPO();
        teamPO.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        return ResultVO.success("获取全部团队成功",teamService.getAllTeam(teamPO));
    }


}
