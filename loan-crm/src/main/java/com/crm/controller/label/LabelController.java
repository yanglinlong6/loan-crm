package com.crm.controller.label;

import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.employee.model.OrgEmployeeBO;
import com.crm.service.label.LabelService;
import com.crm.service.label.model.LabelPO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LabelController {

    private static final Logger LOG = LoggerFactory.getLogger(LabelController.class);

    @Autowired
    LabelService labelService;

    @RequestMapping("/label/all")
    @ResponseBody
    public ResultVO allLabel(){
        OrgEmployeeBO orgEmployee = LoginUtil.getLoginEmployee();
        if(null == orgEmployee)
            ResultVO.fail("请先登录.",null);
        return ResultVO.success("全部标签获取成功",labelService.selectAllLabel(LoginUtil.getLoginEmployee().getOrgId()) );
    }


    @RequestMapping("/label/add")
    @ResponseBody
    public ResultVO addLabel(@RequestBody() LabelPO label){
        if(StringUtils.isBlank(label.getName())){
            return ResultVO.success("创建新标签:缺少参数",null);
        }
        label.setOrgId(LoginUtil.getLoginEmployee().getOrgId());
        labelService.addLabel(label);
        return ResultVO.success("创建新标签成功",null);
    }


    @RequestMapping("/label/del")
    @ResponseBody
    public ResultVO delLabel(@RequestBody() LabelPO label){
        labelService.delLabel(label.getId());
        return ResultVO.success("删除新标签成功",null);
    }

}
