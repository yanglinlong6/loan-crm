package com.daofen.crm.service.city;

import com.alibaba.fastjson.JSONArray;
import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.service.city.dao.CityMapper;
import com.daofen.crm.service.city.model.CityBO;
import com.daofen.crm.service.city.model.CityPO;
import com.daofen.crm.utils.CollectionUtil;
import com.daofen.crm.utils.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 城市服务实现类
 */
@Service
public class CityServiceImpl implements CityService {

    private static final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public List<CityPO> getAll(Byte level) {

        try {
            Object value = stringRedisTemplate.opsForValue().get(CrmConstant.City.Redis.CITY_KEY + level);
            if(null != value) {
                return JSONUtil.toJavaBeanList(value.toString(), CityPO.class);
            }
            List<CityPO> list = cityMapper.selectAll(level);
            if(CollectionUtil.isEmpty(list)) {
                return list;
            }
            stringRedisTemplate.opsForValue().set(CrmConstant.City.Redis.CITY_KEY + level, JSONArray.toJSONString(list),60*60, TimeUnit.SECONDS);
            return list;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            stringRedisTemplate.delete(CrmConstant.City.Redis.CITY_KEY + level);
        }finally {

        }
        return cityMapper.selectAll(level);
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
    private static final Random r = new Random();


	@Override
	public CityBO getMpCityForTree(Byte level) {

        List<CityPO> cityList = cityMapper.selectAllMp(level);
        if(CollectionUtil.isEmpty(cityList)) {
            return new CityBO();
        }
        CityBO root = new CityBO();
        root.setName("root");
        root.setId("0");
        root.setLevel(Byte.valueOf("0"));
        getChildren(root, cityList);

        if(CollectionUtil.isEmpty(root.getChildList())){
            return root;
        }
        List<CityBO> list = root.getChildList();
        int index = r.nextInt(list.size()-1);
        CityBO indexBO = list.get(index);
        CityBO oneBO = list.get(0);
        list.set(0,indexBO);
        list.set(index,oneBO);
        return root;
	}

}
