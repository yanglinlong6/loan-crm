package com.daofen.crm.service.company;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.dao.CompanyMapper;
import com.daofen.crm.service.company.model.CompanyBO;
import com.daofen.crm.service.company.model.CompanyPO;
import com.daofen.crm.service.counselor.CounselorService;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.utils.CollectionUtil;
import com.daofen.crm.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    private static final Logger LOG = LoggerFactory.getLogger(CompanyServiceImpl.class);
    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CounselorService counselorService;

    @Override
    public void getCompanyPage(PageVO<CompanyBO> page) {
        if(null == page.getParam()){
            CompanyBO company = new CompanyBO();
            page.setParam(company);
        }
        Long companyParentId = LoginUtil.getLoginUser().getCompany().getParentId();
        if(null == companyParentId || 0 == companyParentId.longValue()){
            companyParentId = LoginUtil.getLoginUser().getCompany().getId();
        }
        page.getParam().setId(companyParentId);
        List<CompanyBO> list = companyMapper.selectCompanyPage(page);
        page.setData(list);
        getCompanyPageCount(page);
    }

    @Override
    public void getCompanyPageCount(PageVO<CompanyBO> pageVO) {
        pageVO.setTotalCount(companyMapper.selectCompanyPageCount(pageVO));
    }

    @Override
    public List<CompanyPO> getAllCompany(Long parentId) {
        return companyMapper.selectAllCompany(parentId);
    }

    @Override
    public CompanyPO getCompanyById(Long id) {
        return companyMapper.selectCompanyById(id);
    }


    @Override
    public void addCompany(CompanyBO companyBO) {
        CompanyCounselorBO counselorBO = LoginUtil.getLoginUser();
        //新增管理员用户
        CompanyCounselorBO newCounselor = companyBO.getCounselor();
        if(CrmConstant.Role.Type.SUPER == counselorBO.getRole().getType().byteValue()){
            if(CrmConstant.Company.Type.PARENT == companyBO.getType()
                || CrmConstant.Company.Type.LEAGUE == companyBO.getType())
                companyBO.setParentId(counselorBO.getCompanyId());
            else
                throw new CrmException(ResultVO.ResultCode.FAIL,"超级管理员不能添加分公司");
        }else{
            if(CrmConstant.Company.Type.PARENT == companyBO.getType()){
                throw new CrmException(ResultVO.ResultCode.FAIL,"您已经有总公司,不需要重复添加总公司");
            }else if(CrmConstant.Company.Type.PARENT == counselorBO.getCompany().getType()){
                companyBO.setParentId(counselorBO.getCompanyId());
            }else{
                companyBO.setParentId(counselorBO.getCompany().getParentId());
            }
        }
        companyBO.setParentId(counselorBO.getCompanyId());
        CompanyPO companyPO = JSONUtil.toJavaBean(companyBO.toString(),CompanyPO.class);
        companyMapper.insertCompany(companyPO);
        LOG.info("CompanyPO-->{}",companyPO.toString());
        if(StringUtils.isBlank(newCounselor.getName())){
            newCounselor.setName(newCounselor.getMobile());
        }
        newCounselor.setCompany(companyPO);
        newCounselor.setCompanyId(companyPO.getId());
        newCounselor.setStatus(CrmConstant.Counselor.Status.OFFLINE);
        newCounselor.setOpen(CrmConstant.Counselor.Open.CLOSE);
        newCounselor.setType(CrmConstant.Counselor.Type.INNER);
        newCounselor.setUpdateBy(counselorBO.getId().toString());
        LOG.error("新增城市分公司信息：{}",newCounselor.toString());
        counselorService.addCounselor(newCounselor);

    }

    @Override
    public void editCompany(CompanyBO companyPO) {
        companyPO.setUpdateBy(LoginUtil.getLoginUser().getId().toString());
        companyPO.setUpdateDate(new Date());
        companyMapper.updateCompany(companyPO);
    }

    @Override
    public void delCompany(Long id) {
        CompanyPO company = companyMapper.selectCompanyById(id);
        if(null == company)
            return;
        if(CrmConstant.Company.Type.PARENT == company.getType().byteValue())
            throw new CrmException(ResultVO.ResultCode.FAIL,"总公司不能删除,请联系您的客户经理,谢谢");
        if(!CollectionUtil.isEmpty(counselorService.getCounselorByCompanyId(id)))
            throw new CrmException(ResultVO.ResultCode.FAIL,"该公司下还有顾问,请先将顾问移出才能删除该机构!");
        companyMapper.deleteCompanyById(id);
    }
}
