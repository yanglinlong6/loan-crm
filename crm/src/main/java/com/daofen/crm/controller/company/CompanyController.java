package com.daofen.crm.controller.company;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.CompanyService;
import com.daofen.crm.service.company.model.CompanyBO;
import com.daofen.crm.service.company.model.CompanyPO;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.List;


@Validated
@RestController
public class CompanyController extends AbstractController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     *
     * @return ResultVO
     */
    @RequestMapping("/company/all")
    @ResponseBody
    public ResultVO getAllCompany(){
        CompanyCounselorBO account = LoginUtil.getLoginUser();
        List<CompanyPO> data  = companyService.getAllCompany(account.getCompanyId());
//        if(CrmConstant.Company.Type.PARENT == account.getCompany().getType().byteValue()){
//            data = companyService.getAllCompany(account.getCompanyId());
//        }else{
//            data = companyService.getAllCompany(account.getCompany().getParentId());
//        }
        return this.success(data);
    }


    @RequestMapping("/company/list")
    @ResponseBody
    public ResultVO getList(@RequestBody()PageVO<CompanyBO> pageVO){
        companyService.getCompanyPage(pageVO);
        return this.success(pageVO);
    }


    @RequestMapping("/company/add")
    @ResponseBody
    public ResultVO addCompany(@Validated()@RequestBody()CompanyBO companyBO){

        if(null == companyBO){
            return this.failed("请填写公司相信",null);
        }
        companyService.addCompany(companyBO);
        return this.success();
    }

    /**
     * 编辑公司信息
     * @param companyBO 公司业务对象信息
     * @return ResultVO
     */
    @RequestMapping("/company/edit")
    @ResponseBody
    public ResultVO edit(@Validated()@RequestBody()CompanyBO companyBO){
        companyService.editCompany(companyBO);
        return this.success();
    }

    @RequestMapping("/company/del")
    @ResponseBody
    public ResultVO delCompany(@PathParam("id") Long id){
        if(null == id || id <=0)
            return this.failed("请选择要删除的城市分公司",null);
        companyService.delCompany(id);
        return this.success();
    }


}
