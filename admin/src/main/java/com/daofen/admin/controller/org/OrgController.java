package com.daofen.admin.controller.org;

import com.daofen.admin.basic.AbstractController;
import com.daofen.admin.basic.PageVO;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.service.org.OrgService;
import com.daofen.admin.service.org.model.OrgAptitudePO;
import com.daofen.admin.service.org.model.OrgPO;
import com.daofen.admin.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class OrgController extends AbstractController {

    @Autowired
    private OrgService orgService;

    /**
     * 分页接口
     *
     * @param page PageVO<OrgPO>
     * @return ResultVO
     */
    @PostMapping("/org/list")
    @ResponseBody
    public ResultVO page(@RequestBody() PageVO<OrgPO> page) {
        orgService.orgPage(page);
        return this.success(ResultCode.SUC, page);
    }

    @GetMapping("/org/all")
    @ResponseBody
    public ResultVO getAllOrg(@RequestParam("orgName") String orgName) {
        return this.success(ResultCode.SUC, orgService.getAllOrg(orgName));
    }

    /**
     * 机构增加接口
     *
     * @param orgPO
     * @return ResultVO
     */
    @PostMapping("/org/add")
    @ResponseBody
    public ResultVO add(@RequestBody() OrgPO orgPO) {
        orgService.addOrg(orgPO);
        return this.success();
    }

    /**
     * 机构更新接口
     *
     * @param orgPO
     * @return ResultVO
     */
    @PostMapping("/org/update")
    @ResponseBody
    public ResultVO update(@RequestBody() OrgPO orgPO) {
        orgService.updateOrg(orgPO);
        return this.success();
    }


    /**
     * 当日机构需求汇总
     *
     * @return
     */
    @GetMapping("/org/aptitude/need/collect")
    @ResponseBody
    public ResultVO orgAptitudeNeedCollect() {
        String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
        String startDate = date + " 00:00:00";
        String endDate = date + " 23:59:59";
        return this.success(ResultCode.SUC, orgService.getCityNeed(startDate, endDate));
    }

    @PostMapping("/org/aptitude/list")
    @ResponseBody
    public ResultVO orgAptitudePage(@RequestBody() PageVO<OrgAptitudePO> pageVO) {
        orgService.orgAptitudePage(pageVO);
        return this.success(ResultCode.SUC, pageVO);
    }


    @PostMapping("/org/aptitude/add")
    @ResponseBody
    public ResultVO orgAptitudeAdd(@RequestBody() OrgAptitudePO orgAptitudePO) {
        orgService.addOrgAptitude(orgAptitudePO);
        return this.success(ResultCode.SUC, "机构配量新增成功");
    }


    @PostMapping("/org/aptitude/update")
    @ResponseBody
    public ResultVO orgAptitudeUpdate(@RequestBody() OrgAptitudePO orgAptitudePO) {
        orgService.updateOrgAptitude(orgAptitudePO);
        return this.success(ResultCode.SUC, "机构配量更新成功");
    }

    @PostMapping("/org/aptitude/changeStatus")
    @ResponseBody
    public ResultVO changeStatus(@RequestBody() OrgAptitudePO orgAptitudePO) {
        orgService.changeStatus(orgAptitudePO);
        return this.success(ResultCode.SUC, "修改规则状态成功");
    }
}
