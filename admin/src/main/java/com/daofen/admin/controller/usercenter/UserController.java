package com.daofen.admin.controller.usercenter;

import com.daofen.admin.basic.AbstractController;
import com.daofen.admin.basic.PageVO;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.service.user.UserService;
import com.daofen.admin.service.user.model.DayStatisticsPO;
import com.daofen.admin.service.user.model.HandOutUserPO;
import com.daofen.admin.service.user.model.HandOutUserPO;
import com.daofen.admin.service.user.model.LinkOrgVO;
import com.daofen.admin.service.user.model.TimeStatisticsPO;
import com.daofen.admin.service.user.model.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户管理模块
 *
 * @author Yanglinlong
 * @date 2023/11/7 20:36
 */
@RestController
public class UserController extends AbstractController {

    @Autowired
    private UserService userService;

    @PostMapping("/user/getList")
    @ResponseBody
    public ResultVO getList(@RequestBody() PageVO<UserPO> pageVO) {
        userService.getList(pageVO);
        return this.success(ResultCode.SUC, pageVO);
    }

    @PostMapping("/user/save")
    @ResponseBody
    public ResultVO saveUser(@RequestBody() UserPO userPO) {
        userService.saveUser(userPO);
        return this.success(ResultCode.SUC, "新增用户成功");
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ResultVO updateUser(@RequestBody() UserPO userPO) {
        userService.updateUser(userPO);
        return this.success(ResultCode.SUC, "修改用户成功");
    }

    @PostMapping("/user/delete")
    @ResponseBody
    public ResultVO deleteUser(@RequestBody() UserPO userPO) {
        userService.deleteUser(userPO);
        return this.success(ResultCode.SUC, "删除用户成功");
    }

    @PostMapping("/user/findOne")
    @ResponseBody
    public ResultVO findOneUser(@RequestBody() UserPO userPO) {
        userService.findOneUser(userPO);
        return this.success(ResultCode.SUC, "详情查询成功");
    }

    @PostMapping("/user/resetPassword")
    @ResponseBody
    public ResultVO resetPassword(@RequestBody() UserPO userPO) {
        userService.resetPassword(userPO);
        return this.success(ResultCode.SUC, "重置密码成功");
    }

    @PostMapping("/user/linkOrg")
    @ResponseBody
    public ResultVO linkOrg(@RequestBody() LinkOrgVO linkOrgVO) {
        userService.linkOrg(linkOrgVO);
        return this.success(ResultCode.SUC, "用户指定组织成功");
    }

    @PostMapping("/user/getBindLinkOrg")
    @ResponseBody
    public ResultVO getBindLinkOrg(@RequestBody() LinkOrgVO linkOrgVO) {
        List<Integer> orgIdList = userService.getBindLinkOrg(linkOrgVO);
        return this.success(ResultCode.SUC, orgIdList);
    }

    @PostMapping("/user/getHandOutUserList")
    @ResponseBody
    public ResultVO getHandOutUserList(@RequestBody() PageVO<HandOutUserPO> pageVO) {
        userService.getHandOutUserList(pageVO);
        return this.success(ResultCode.SUC, pageVO);
    }

    @PostMapping("/user/getDayStatisticsList")
    @ResponseBody
    public ResultVO getDayStatisticsList() {
        List<DayStatisticsPO> dayStatisticsPOList = userService.getDayStatisticsList();
        return this.success(ResultCode.SUC, dayStatisticsPOList);
    }

    @PostMapping("/user/getTimeStatisticsList")
    @ResponseBody
    public ResultVO getTimeStatisticsList() {
        List<TimeStatisticsPO> timeStatisticsPOList = userService.getTimeStatisticsList();
        return this.success(ResultCode.SUC, timeStatisticsPOList);
    }

    @PostMapping("/user/getDayHandOutStatisticsList")
    @ResponseBody
    public ResultVO getDayHandOutStatisticsList() {
        List<DayStatisticsPO> dayStatisticsPOList = userService.getDayHandOutStatisticsList();
        return this.success(ResultCode.SUC, dayStatisticsPOList);
    }

    @PostMapping("/user/getTimeHandOutStatisticsList")
    @ResponseBody
    public ResultVO getTimeHandOutStatisticsList() {
        List<TimeStatisticsPO> timeStatisticsPOList = userService.getTimeHandOutStatisticsList();
        return this.success(ResultCode.SUC, timeStatisticsPOList);
    }
}
