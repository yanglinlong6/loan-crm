package com.daofen.crm.controller.company;

import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.company.TeamService;
import com.daofen.crm.service.company.model.TeamBO;
import com.daofen.crm.service.company.model.TeamPO;
import com.daofen.crm.service.counselor.CounselorService;
import com.daofen.crm.utils.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;

@RestController
public class TeamController extends AbstractController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CounselorService counselorService;

    @RequestMapping("/team/list")
    @ResponseBody
    public ResultVO list(PageVO<TeamBO> pageVO){
        teamService.getTeamPage(pageVO);
        return this.success(pageVO);
    }

    @RequestMapping("/team/add")
    @ResponseBody
    public ResultVO addTeam(@Validated() @RequestBody() TeamPO teamPO){
        teamService.addTeam(teamPO);
        return this.success();
    }

    @RequestMapping("/team/edit")
    @ResponseBody
    public ResultVO editTeam(@Validated() @RequestBody() TeamPO teamPO){
        teamService.saveTeam(teamPO);
        return this.success();
    }

    @RequestMapping("/team/del")
    @ResponseBody
    public ResultVO delTeam(@PathParam("id") Long id){
        if(null == id || id <= 0 )
            return this.failed("请选择要删除的团队",null);
        if(!CollectionUtil.isEmpty(counselorService.getTeamCounselorByTeamId(id))){
            throw  new CrmException(ResultVO.ResultCode.FAIL,"请删除该团队下的所有顾问");
        }
        teamService.delTeam(id);
        return this.success();
    }

    @GetMapping("/team/by/companyId/shopId")
    @ResponseBody
    public ResultVO getAllTeam(@RequestParam("companyId") Long companyId,
                               @RequestParam(name = "shopId")Long shopId){
        if(companyId <=0 || shopId <=0)
            return this.failed("请选择正确的公司或门店",null);
        return this.success(teamService.getShopTeam(companyId,shopId));
    }

}
