package com.daofen.crm.service.company;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.company.model.CompanyBO;
import com.daofen.crm.service.company.model.CompanyPO;
import com.daofen.crm.utils.CollectionUtil;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 公司接口service
 */
public interface CompanyService {



    /**
     * 公司列表：分页查询
     * @param pageVO
     */
    void getCompanyPage(PageVO<CompanyBO> pageVO);

    /**
     * 公司泪飙分页
     * @param pageVO
     */
    void getCompanyPageCount(PageVO<CompanyBO> pageVO);

    /**
     * 获取总公司下的所有公司，包含总公司
     * @param parentId 总公司id
     * @return List<CompanyPO>
     */
    List<CompanyPO> getAllCompany(Long parentId);

    /**
     * 获取公司信息对象
     * @param id 公司id
     * @return CompanyPO
     */
    CompanyPO getCompanyById(Long id);

    /**
     * 增加机构，需要判断是总公司，还是分公司
     * @param companyPO CompanyPO
     */
    @Transactional()
    void addCompany(CompanyBO companyPO);

    @Transactional()
    void editCompany(CompanyBO companyPO);

    /**
     * 删除城市分公司
     * @param id 城市分公司id
     */
    void delCompany(Long id);


    /**
     * 把总公司和所有分公司的id以","拼接，比如：1,2,3,4
     * @param companyId 总公司id
     * @return 把总公司和所有分公司的id以","拼接，比如：1,2,3,4
     */
    default String getAllCompanyIds(Long companyId){
        List<CompanyPO> companyPOS = getAllCompany(companyId);
        if(CollectionUtil.isEmpty(companyPOS))
            return companyId.toString();
        else {
            StringBuffer companyIds = new StringBuffer();
            for(int i=0;i<companyPOS.size();i++){
                if(i == 0)
                    companyIds.append(companyPOS.get(i).getId());
                else companyIds.append(",").append(companyPOS.get(i).getId());
            }
            return companyIds.toString();
        }
    }


}
