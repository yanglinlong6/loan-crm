package com.daofen.admin.service.city;


import com.daofen.admin.service.city.model.CityBO;
import com.daofen.admin.service.city.model.CityPO;

import java.util.List;

public interface CityService {


    /**
     * 获取所有城市列表
     *
     * @param level 级别(1省份,2城市,3区县) ；如果level=3，则表示获取三级：省份，城市，区(县)
     * @return List<CityPO>
     */
    List<CityPO> getAll(Byte level);

    /**
     *  查询城市
     * @param city 城市名称
     * @return
     */
    CityPO getCityPO(String city);

    /**
     * 获取城市列表，并组装成树形接口
     *
     * @param level 级别(1省份,2城市,3区县) ；如果level=3，则表示获取三级：省份，城市，区(县)
     * @return CityBO
     */
    CityBO getCityForTree(Byte level);

    
    /**
     * 获取投放城市列表，并组装成树形接口
     *
     * @param level 级别(1省份,2城市,3区县) ；如果level=3，则表示获取三级：省份，城市，区(县)
     * @return CityBO
     */
    CityBO getMpCityForTree(Byte level);
}
