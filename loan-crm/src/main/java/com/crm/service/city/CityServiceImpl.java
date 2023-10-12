package com.crm.service.city;

import com.alibaba.fastjson.JSONArray;
import com.crm.common.CrmConstant;
import com.crm.service.city.dao.CityMapper;
import com.crm.service.city.model.CityPO;
import com.crm.util.JSONUtil;
import com.crm.util.ListUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CityServiceImpl implements CityService{

    private static final Logger log = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityMapper cityMapper;

    @Autowired
    private StringRedisTemplate template;

    @Override
    public List<CityPO> getAll(Byte level) {
        List<CityPO> list = new ArrayList<>();
        String key = CrmConstant.Config.City.CITY_KEY + level;
        try {
            Object value = template.opsForValue().get(key);
            if(null != value) {
                return JSONUtil.toJavaBeanList(value.toString(), CityPO.class);
            }
            list = cityMapper.selectAll(level);
            if(ListUtil.isEmpty(list)) {
                return list;
            }
            template.opsForValue().set(key, JSONArray.toJSONString(list),1, TimeUnit.HOURS);
            return list;
        } catch(Exception e) {
            log.error(e.getMessage(), e);
            template.delete(key);
        }
        return list;
    }
}
