package com.daofen.crm.service.company.dao;


import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.company.model.CompanyBO;
import com.daofen.crm.service.company.model.CompanyPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface CompanyMapper {

    /**
     * 分页:当前页数据列表
     * @param pageVO PageVO<CompanyBO>
     * @return List<CompanyBO>
     */
    List<CompanyBO> selectCompanyPage(PageVO<CompanyBO> pageVO);

    /**
     * 分页：总数量
     * @param pageVO PageVO<CompanyBO>
     * @return int
     */
    int selectCompanyPageCount(PageVO<CompanyBO> pageVO);

    /**
     * 查询总公司下的所有分公司列表，包含自己
     * @param parentId 总公司id
     * @return List<CompanyPO>
     */
    List<CompanyPO> selectAllCompany(Long parentId);

    int deleteCompanyById(Long id);

    int insertCompany(CompanyPO record);

    CompanyPO selectCompanyById(Long id);

    int updateCompany(CompanyPO record);

}