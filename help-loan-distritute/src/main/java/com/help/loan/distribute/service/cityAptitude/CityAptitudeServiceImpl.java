package com.help.loan.distribute.service.cityAptitude;

import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.service.cityAptitude.dao.CityAptitudeRatePOMapper;
import com.help.loan.distribute.service.cityAptitude.model.CityAptitudeRatePO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CityAptitudeServiceImpl implements CityAptitudeService {

    private static final Logger LOG = LoggerFactory.getLogger(CityAptitudeServiceImpl.class);

    @Autowired
    private CityAptitudeRatePOMapper cityAptitudeRatePOMapper;

    @Override
    public void updateCityAptitudeRate(String dateString) {
        String startDate = dateString + " 00:00:00";
        String endDate = dateString + " 23:59:59";
        List<CityAptitudeRatePO> list = cityAptitudeRatePOMapper.selectCurrentCityAptitudeRate(dateString,startDate,endDate);
        if(CollectionUtil.isEmpty(list)) {
            LOG.info("更新城市资质占比：{}--{}--{},当前还没有城市资质占比数据",dateString,startDate,endDate);
            return;
        }
        for(CityAptitudeRatePO po : list){
            CityAptitudeRatePO cityAptitudeRatePO = cityAptitudeRatePOMapper.selectCityAptitudeRatePO(dateString,po.getCity());
            if(null == cityAptitudeRatePO)
                cityAptitudeRatePOMapper.insertCityAptitudeRate(po);
            else cityAptitudeRatePOMapper.updateCityAptitudeRate(po);
        }
    }

    @Override
    public CityAptitudeRatePO getCityAptitudeRate(Long orgId, String city, String startDate, String endDate) {
        if(null == orgId || StringUtils.isBlank(city)){
            return null;
        }
        // 如果startDate或者endDate为空，则取当天的开始日期和结束日期
        if(StringUtils.isBlank(startDate) || StringUtils.isBlank(endDate)){
            String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
            startDate = date + " 00:00:00";
            endDate = date + " 23:59:59";
        }
        return cityAptitudeRatePOMapper.selectOrgCityAptitudeRate(orgId,city,startDate,endDate);
    }


}
