package com.daofen.admin.controller.usercenter;

import com.daofen.admin.basic.AbstractController;
import com.daofen.admin.basic.PageVO;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.service.user.UserService;
import com.daofen.admin.service.user.model.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
}
