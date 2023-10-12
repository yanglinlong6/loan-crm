package com.daofen.admin.service.city;

import com.alibaba.fastjson.JSONArray;
import com.daofen.admin.service.city.dao.CityDao;
import com.daofen.admin.service.city.model.CityBO;
import com.daofen.admin.service.city.model.CityPO;
import com.daofen.admin.utils.CollectionUtil;
import com.daofen.admin.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 城市服务实现类
 */
@Service
public class CityServiceImpl implements CityService {

    private static final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityDao cityDao;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String CITY_KEY = "city_list_";

    @Override
    public List<CityPO> getAll(Byte level) {

        try {
            Object value = stringRedisTemplate.opsForValue().get(CITY_KEY + level);
            if(null != value) {
                return JSONUtil.toJavaBeanList(value.toString(), CityPO.class);
            }
            List<CityPO> list = cityDao.selectAll(level);
            if(CollectionUtil.isEmpty(list)) {
                return list;
            }
            stringRedisTemplate.opsForValue().set(CITY_KEY + level, JSONArray.toJSONString(list),60*60, TimeUnit.SECONDS);
            return list;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            stringRedisTemplate.delete(CITY_KEY + level);
        }finally {

        }
        return cityDao.selectAll(level);
    }

    @Override
    public CityPO getCityPO(String city) {
        if(StringUtils.isBlank(city))
            return null;
        return cityDao.selectCity(city);
    }

    @Override
    public CityBO getCityForTree(Byte level) {

        List<CityPO> cityList = getAll(level);
        if(CollectionUtil.isEmpty(cityList)) {
            return new CityBO();
        }
        CityBO root = new CityBO();
        root.setName("root");
        root.setId("0");
        root.setLevel(Byte.valueOf("0"));
        getChildren(root, cityList);
        return root;
    }

    private void getChildren(CityBO root, List<CityPO> cityList) {
        List<CityBO> childList = new ArrayList<>();
        for(CityPO po : cityList) {
            if(!po.getParentId().equals(root.getId()))
                continue;
            CityBO city = new CityBO();
            BeanUtils.copyProperties(po, city);
            getChildren(city, cityList);
            childList.add(city);
        }
        root.setChildList(childList);
    }

	@Override
	public CityBO getMpCityForTree(Byte level) {

        List<CityPO> cityList = cityDao.selectAllMp(level);
        if(CollectionUtil.isEmpty(cityList)) {
            return new CityBO();
        }
        CityBO root = new CityBO();
        root.setName("root");
        root.setId("0");
        root.setLevel(Byte.valueOf("0"));
        getChildren(root, cityList);
        return root;
	}
}
