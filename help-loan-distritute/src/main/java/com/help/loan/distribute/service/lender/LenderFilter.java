package com.help.loan.distribute.service.lender;

import com.help.loan.distribute.service.lender.model.LenderPO;
import com.loan.cps.entity.Session;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LenderFilter {

    public static List<LenderPO> filter(Integer quota, Session session) {
        List<LenderPO> cacheList = LenderCache.getCacheList();
        List<LenderPO> keySet = cacheList.stream().filter(po -> {
            Set<String> sendFristSet = session.getSendFristSet();
            if(sendFristSet == null) {
                return true;
            }
            return !sendFristSet.contains(po.getLenderId());
        }).sorted(Comparator.comparing((LenderPO po) -> {
            return Math.abs(po.getSettlement() - quota);
        })).collect(Collectors.toList());
        if(keySet == null || keySet.isEmpty()) {
            return new ArrayList<>();
        }
        List<LenderPO> result = new ArrayList<>();
        ;
        for(LenderPO s : keySet) {
            if(result.size() < 3) {
                result.add(s);
            }
        }
        session.getSendFristSet().addAll(result.stream().collect(Collectors.groupingBy(LenderPO :: getLenderId)).keySet());
        return result;
    }

}
