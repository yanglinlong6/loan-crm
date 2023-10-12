package com.daofen.crm.controller.company;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.TeamService;
import com.daofen.crm.service.counselor.CounselorService;
import com.daofen.crm.service.counselor.PermissionService;
import com.daofen.crm.service.counselor.RoleService;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.counselor.model.PermissionBO;
import com.daofen.crm.service.counselor.model.PermissionPO;
import com.daofen.crm.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * 顾问模块控制器
 */
@RestController
public class CounselorController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(CounselorController.class);

    @Autowired
    private CounselorService counselorService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TeamService teamService;

    @RequestMapping("/counselor/permission/list")
    @ResponseBody
    public  ResultVO getPermissionList(){
//        permissionService.getRolePermissionList(LoginUtil.getLoginThreadLocal().getRoleId());
        List<PermissionPO> permissions = LoginUtil.getLoginUser().getRole().getPermissions();
        return this.success(permissionService.toRoot(JSONUtil.toJavaBeanList(JSONUtil.toString(permissions), PermissionBO.class)));
    }

    /**
     * 过去团队下的所有顾问列表
     * @return ResultVO
     */
    @RequestMapping("/counselor/team/list")
    @ResponseBody
    public ResultVO teamList(@PathParam("teamId") Long teamId){
        if(null == teamId || teamId <= 0)
            return this.failed("团队id只能是正整数",null);
        return this.success(counselorService.getTeamCounselorByTeamId(teamId));
    }

    /**
     * 顾问分页列表
     * @param pageVO PageVO<CompanyCounselorBO>
     * @return ResultVO
     */
    @ResponseBody
    @RequestMapping("/counselor/list")
    public ResultVO list(@RequestBody()PageVO<CompanyCounselorBO> pageVO){
        log.info("顾问分页参数：{}",pageVO.toString());
        counselorService.getCounselorPage(pageVO);
        return this.success(pageVO);
    }

    @RequestMapping("/counselor/add")
    @ResponseBody
    public ResultVO add(@Validated()@RequestBody()CompanyCounselorBO counselorBO){

        counselorService.addCounselor(counselorBO);
        return this.success();
    }


    @ResponseBody
    @RequestMapping("/counselor/edit")
    public ResultVO edit(@Validated()@RequestBody()CompanyCounselorBO counselorBO){
        counselorService.updateCounselor(counselorBO);
        return this.success();
    }

    @ResponseBody
    @RequestMapping("/counselor/del")
    public ResultVO del(@PathParam("id") Long id){
        if(null == id || id <= 0 )
            return this.failed("请选择要删除的门店",null);
        counselorService.delCounselor(id);
        return this.success();
    }






}
