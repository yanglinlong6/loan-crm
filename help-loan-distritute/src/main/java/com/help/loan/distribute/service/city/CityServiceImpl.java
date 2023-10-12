package com.help.loan.distribute.service.city;

import com.alibaba.fastjson.JSONArray;
import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.common.utils.JSONUtil;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.city.dao.CityMapper;
import com.help.loan.distribute.service.city.model.CityBO;
import com.help.loan.distribute.service.city.model.CityPO;
import org.apache.commons.lang3.StringUtils;
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

    /**
     * 城市组列表缓存前缀：city_list_
     */
    String CITY_KEY = "city_list_";

    @Override
    public List<CityPO> getAll(Byte level) {

        try {
            String key = CITY_KEY + level;
            Object value = stringRedisTemplate.opsForValue().get(key);
            if(null != value) {
                return JSONUtil.toJavaBeanList(value.toString(), CityPO.class);
            }
            List<CityPO> list = cityMapper.selectAll(level);
            if(CollectionUtil.isEmpty(list)) {
                return list;
            }
            stringRedisTemplate.opsForValue().set(key, JSONArray.toJSONString(list),60*60, TimeUnit.SECONDS);
            return list;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            stringRedisTemplate.delete(CITY_KEY + level);
        }
        return cityMapper.selectAll(level);
    }

    @Override
    public String getProvince(String city) {
        if(StringUtils.isBlank(city))
            return "";
        if(JudgeUtil.in(city,"北京市","上海市","重庆市","天津市"))
            return city;
        return cityMapper.selectProvince(city);
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



}
