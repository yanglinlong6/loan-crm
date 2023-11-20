package com.daofen.admin.service.org;

import com.daofen.admin.basic.AdminException;
import com.daofen.admin.basic.PageVO;
import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.config.interceptor.LoginUtil;
import com.daofen.admin.service.city.CityService;
import com.daofen.admin.service.org.dao.OrgDao;
import com.daofen.admin.service.org.model.CityNeedPO;
import com.daofen.admin.service.org.model.OrgAptitudePO;
import com.daofen.admin.service.org.model.OrgPO;
import com.daofen.admin.utils.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * OrgService接口实现类
 */
@Service
public class OrgServiceImpl implements OrgService {

    private static final Logger log = LoggerFactory.getLogger(OrgServiceImpl.class);

    @Autowired
    private OrgDao orgDao;

    @Autowired
    private CityService cityService;


    @Override
    public void orgPage(PageVO<OrgPO> page) {
        Integer count = orgDao.selectOrgListCountByPage(page);
        page.setData(orgDao.selectOrgListByPage(page));
        page.setTotalCount(count);
    }

    @Override
    public List<OrgPO> getAllOrg(String orgName) {
        return orgDao.selectAllOrg(orgName);
    }

    @Override
    public void addOrg(OrgPO orgPO) {
        if (null == orgPO || StringUtils.isBlank(orgPO.getOrgName()) || StringUtils.isBlank(orgPO.getOrgNickname())) {
            throw new AdminException(ResultCode.FAID, "新增机构信息：参数错误");
        }
        synchronized (this) {
            OrgPO oldOrg = orgDao.selectOrgByName(orgPO);
            if (null != oldOrg) {
                throw new AdminException(ResultCode.FAID, "机构名称已存在");
            }
            // 将最新的机构id+1，做为新机构的机构id
            Long maxOrgId = orgDao.selectMaxOrg();
            if (Objects.isNull(maxOrgId)) {
                orgPO.setOrgId(1L);
            } else {
                orgPO.setOrgId(maxOrgId + 1);
            }
            orgDao.insertOrg(orgPO);
        }
    }

    @Override
    public void updateOrg(OrgPO orgPO) {
        if (null == orgPO || null == orgPO.getOrgId() || StringUtils.isBlank(orgPO.getOrgName()) || StringUtils.isBlank(orgPO.getOrgNickname())) {
            throw new AdminException(ResultCode.FAID, "修改机构信息：参数错误");
        }
        synchronized (this) {
            OrgPO oldOrg = orgDao.selectOrgByName(orgPO);
            if (null == oldOrg) {
                orgDao.updateOrg(orgPO);
            } else {
                if (oldOrg.getOrgId().longValue() == orgPO.getOrgId().longValue()) {
                    orgDao.updateOrg(orgPO);
                } else
                    throw new AdminException(ResultCode.FAID, "机构名称已存在");
            }
        }
    }

    @Override
    public OrgPO getOrg(Long orgId) {
        if (null == orgId)
            return null;
        return orgDao.selectOrgByOrgId(orgId);
    }

    @Override
    public List<CityNeedPO> getCityNeed(String startDate, String endDate) {
        if (StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate))
            return null;
        List<CityNeedPO> all = orgDao.selectCityNeed(startDate, endDate);
        if (CollectionUtil.isEmpty(all))
            return null;
        Map<String, CityNeedPO> map = new LinkedHashMap<>();
        for (CityNeedPO cityNeed : all) {
            if (map.containsKey(cityNeed.getCity())) {
                CityNeedPO po = map.get(cityNeed.getCity());
                po.setLimitCount(po.getLimitCount() + cityNeed.getLimitCount());
                po.setSendCount(po.getSendCount() + cityNeed.getSendCount());
            } else {
                map.put(cityNeed.getCity(), cityNeed);
            }
        }
        all.clear();
        Set<String> set = map.keySet();
        for (String key : set) {
            all.add(map.get(key));
        }
        return all;
    }

    @Override
    public void orgAptitudePage(PageVO<OrgAptitudePO> page) {
        page.setData(orgDao.selectOrgAptitudePage(page));
        page.setTotalCount(orgDao.selectOrgAptitudePageCount(page));
        log.info("机构配量分页列表总页数：{}", page.getTotalPage());
    }

    @Override
    public void addOrgAptitude(OrgAptitudePO orgAptitudePO) {
        if (null == orgAptitudePO)
            throw new AdminException(ResultCode.FAID, "机构城市配量参数对象是空的。");
        //验证参数
        orgAptitudePO.checkWeek().checkLimitTime().checkLimitCount().checkSingleIncome().checkOrgId(orgDao).checkCity(cityService);
        synchronized (this) {
            List<OrgAptitudePO> orgAptitudePOList = orgDao.selectOrgAptitudeByOrgId(orgAptitudePO.getOrgId());
            if (!CollectionUtil.isEmpty(orgAptitudePOList)) {
                for (OrgAptitudePO po : orgAptitudePOList) {
                    if (po.getCity().equals(orgAptitudePO.getCity()))
                        throw new AdminException(ResultCode.FAID, "机构城市[" + orgAptitudePO.getCity() + "]已配置，请勿重新配置");
                }
            }
            int weight = 0;
            if (0 == orgAptitudePO.getLimitCount() || orgAptitudePO.getSingleIncome().intValue() == 0) {
                weight = 0;
            } else {
                weight = 1200000 / orgAptitudePO.getLimitCount() / orgAptitudePO.getSingleIncome().intValue();
            }
            orgAptitudePO.setWeight(weight);
            orgAptitudePO.setCreateBy(LoginUtil.getLoginCache().getUsername());
            // 默认为正常状态
            orgAptitudePO.setStatus(0);
            orgDao.insertOrgAptitude(orgAptitudePO);
        }
    }


    @Override
    public void updateOrgAptitude(OrgAptitudePO orgAptitudePO) {
        if (null == orgAptitudePO || null == orgAptitudePO.getOrgId() || StringUtils.isBlank(orgAptitudePO.getCity())) {
            throw new AdminException(ResultCode.FAID, "更新机构配量参数错误");
        }
        //验证参数
        orgAptitudePO.checkWeek().checkLimitTime().checkLimitCount().checkSingleIncome().checkOrgId(orgDao).checkCity(cityService);
        synchronized (this) {
            if (0 == orgAptitudePO.getLimitCount() || orgAptitudePO.getSingleIncome().intValue() == 0) {
                orgAptitudePO.setWeight(0);
            } else
                orgAptitudePO.setWeight(1300000 / orgAptitudePO.getLimitCount() / orgAptitudePO.getSingleIncome().intValue());
            orgAptitudePO.setOrgName(null);
            orgDao.updateOrgAptitude(orgAptitudePO);
        }

    }

    @Override
    public void changeStatus(OrgAptitudePO orgAptitudePO) {
        orgDao.changeStatus(orgAptitudePO.getId(), orgAptitudePO.getStatus());
    }

    @Override
    public void changeUseLegacyFlag(OrgAptitudePO orgAptitudePO) {
        orgDao.changeUseLegacyFlag(orgAptitudePO.getId(), orgAptitudePO.getUseLegacyFlag());
    }

    @Override
    public void deleteOrgAptitude(OrgAptitudePO orgAptitudePO) {
        orgDao.deleteOrgAptitude(orgAptitudePO.getId());
    }

    @Override
    public void deleteOrg(OrgPO orgPO) {
        orgDao.deleteOrg(orgPO.getId());
    }
}
