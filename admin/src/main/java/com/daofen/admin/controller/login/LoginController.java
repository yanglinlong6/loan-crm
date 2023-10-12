package com.daofen.admin.controller.login;

import com.daofen.admin.basic.AbstractController;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.config.interceptor.LoginUtil;
import com.daofen.admin.service.user.UserService;
import com.daofen.admin.service.user.model.UserBO;
import com.daofen.admin.utils.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;


    @PostMapping("/login")
    @ResponseBody
    public ResultVO login(@RequestBody()UserBO userBO, HttpServletResponse response){
        if(null == userBO || StringUtils.isBlank(userBO.getUsername()) || StringUtils.isBlank(userBO.getPassword()))
            return this.failed(ResultCode.FAID,"用户名和密码必填");
        UserBO user = userService.getUserBOByUsername(userBO.getUsername());
        if(null == user || !MD5Util.getMd5String(userBO.getPassword()).equals(user.getPassword()))
            return this.failed(ResultCode.FAID,"用户名或密码错误");
        return this.success(ResultCode.SUC.getCode(),ResultCode.SUC.getDesc(),LoginUtil.writeLoginCookie(response,userBO));
    }

}
