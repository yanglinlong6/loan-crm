package com.crm.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ListUtil {


    public static boolean isEmpty(List<?> list) {
        if(null == list || list.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Set<?> set) {
        if(null == set || set.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Map<?, ?> map) {
        if(null == map || map.isEmpty()) {
            return true;
        }
        return false;
    }

}
