/**
 *
 */
package com.help.loan.distribute.service.lender;

import com.help.loan.distribute.service.lender.model.LenderPO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KZM
 *
 */
public class LenderCache {

    private static List<LenderPO> cacheList = new ArrayList<>();

    public static List<LenderPO> getCacheList() {
        return cacheList;
    }

    public static void setCacheList(List<LenderPO> cacheList) {
        LenderCache.cacheList = cacheList;
    }

}
