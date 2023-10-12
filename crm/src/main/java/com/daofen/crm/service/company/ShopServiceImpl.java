package com.daofen.crm.service.company;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.company.dao.ShopPOMapper;
import com.daofen.crm.service.company.model.ShopBO;
import com.daofen.crm.service.company.model.ShopPO;
import com.daofen.crm.service.counselor.CounselorService;
import com.daofen.crm.utils.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 门店service实现
 */
@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopPOMapper shopPOMapper;

    @Autowired
    private TeamService teamService;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CounselorService counselorService;

    @Override
    public void getPage(PageVO<ShopBO> pageVO) {
        if(null == pageVO.getParam()){
            ShopBO shopBO = new ShopBO();
            pageVO.setParam(shopBO);
        }
        // 如果是总公司，则查询出总公司下的所有分公司,否则是分公司，则查询出分公司下的所有门店
        if(CrmConstant.Company.Type.PARENT == LoginUtil.getLoginUser().getCompany().getType().byteValue()){
            pageVO.getParam().setCompanyIds(companyService.getAllCompanyIds(LoginUtil.getLoginUser().getCompanyId()));
        }else
            pageVO.getParam().setCompanyIds(LoginUtil.getLoginUser().getCompanyId().toString());
        pageVO.setData(shopPOMapper.selectShopPage(pageVO));
        pageVO.setTotalCount(shopPOMapper.selectShopPageCount(pageVO));
    }

    @Override
    public void addShop(ShopPO shopPO) {
        if(null == shopPOMapper.selectShopByCompanyIdAndName(shopPO.getCompanyId(),shopPO.getName())){
            shopPOMapper.insertShop(shopPO);
            return;
        }
        throw new CrmException(ResultVO.ResultCode.FAIL,"门店名称："+shopPO.getName()+" 已存在！");
    }

    @Override
    public void saveShop(ShopPO shopPO) {
        if(StringUtils.isBlank(shopPO.getName()))
            throw new CrmException(ResultVO.ResultCode.FAIL,"门店名称是必填的");
        ShopPO oldShop = shopPOMapper.selectShopByName(shopPO.getName());
        if(null == oldShop){
            shopPOMapper.updateShop(shopPO);
            return;
        }
        if(oldShop.getId() == shopPO.getId()){
            shopPOMapper.updateShop(shopPO);
            return;
        }
        throw new CrmException(ResultVO.ResultCode.FAIL,"门店名称已存在");
    }

    @Override
    public void delShop(Long id) {
        if(!CollectionUtil.isEmpty(teamService.getShopTeam(LoginUtil.getLoginUser().getCompanyId(),id))){
            throw new CrmException(ResultVO.ResultCode.FAIL,"请先删除该门店下的所有顾问以及团队");
        }
        if(!CollectionUtil.isEmpty(counselorService.getCounselorByShopId(id))){
            throw new CrmException(ResultVO.ResultCode.FAIL,"请先删除该门店下的所有顾问以及团队");
        }
        shopPOMapper.deleteShop(id);
    }

    @Override
    public List<ShopPO> getShopListByCompanyId(Long companyId) {
        if(null == companyId || companyId < 0)
            throw new CrmException(ResultVO.ResultCode.FAIL,"公司id不能为空",null);
        return shopPOMapper.selectCompanyShops(companyId);
    }
}
