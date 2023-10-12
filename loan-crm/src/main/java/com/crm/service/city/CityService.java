package com.crm.service.city;

import com.crm.service.city.model.CityBO;
import com.crm.service.city.model.CityPO;
import com.crm.util.ListUtil;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public interface CityService {

    public List<CityPO> getAll(Byte level);

    default CityBO getCityForTree(List<CityPO> cityList) {

        if(ListUtil.isEmpty(cityList)) {
            return new CityBO();
        }
        CityBO root = new CityBO();
        root.setName("root");
        root.setId("0");
        root.setLevel(Byte.valueOf("0"));
        getChildren(root, cityList);
        return root;
    }

    default void getChildren(CityBO root, List<CityPO> cityList) {
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
}
