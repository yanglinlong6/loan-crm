package com.crm.controller.login;

import com.crm.common.CrmConstant;
import com.crm.common.ResultVO;
import com.crm.service.employee.EmployeeService;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.employee.model.OrgEmployeePO;
import com.crm.util.JSONUtil;
import com.crm.util.JudgeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录控制器
 */
@RestController
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmployeeService employeeService;

    /**
     * 登录接口
     * @param employee OrgEmployeePO
     * @return  ResultVO
     */
    @PostMapping("/login")
    @ResponseBody
    public ResultVO login(@RequestBody()OrgEmployeePO employee, HttpServletRequest request, HttpServletResponse response){
        String token = LoginUtil.readCookie(request);
        if(StringUtils.isBlank(token)){
            token = "";
        }
        // 判断员工是否已经被删除了
        OrgEmployeeBO bo = employeeService.login(employee.getPhone(),employee.getPassword());
        if(null == bo){
            ResultVO.fail("账户或密码错误!",null);
        }
        //判断改主体账户是否开启了休息模式
        byte state = LoginUtil.judgeSwitch(bo.getOrgId());
        if(CrmConstant.Switch.NO == state && !LoginUtil.isAdmin(bo)){
            return ResultVO.fail("已开启休息模式,请联系管理员",state);
        }
        String value = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isNotBlank(value)){
            OrgEmployeeBO bo2 = JSONUtil.toJavaBean(value,OrgEmployeeBO.class);
            return ResultVO.success("已经登陆，不用再次登陆",bo2.getRole().getPermissions());
        }
        token = LoginUtil.writeCookie(response,bo);
        log.info("登陆成功：{},token-{}",bo.toString(),token);
        return ResultVO.success("登录成功",token);
    }


    @PostMapping("/permissions")
    @ResponseBody
    public ResultVO getPermissionList(){
        return ResultVO.success("获取权限列表成功",LoginUtil.getLoginEmployee().getRole().getPermissions());
    }



    @PostMapping("/login/out")
    @ResponseBody
    public ResultVO loginOut(HttpServletRequest request){
        LoginUtil.loginOut(request);
        log.info("退出登陆成功");
        return ResultVO.success("退出登陆成功",null);
    }

}
