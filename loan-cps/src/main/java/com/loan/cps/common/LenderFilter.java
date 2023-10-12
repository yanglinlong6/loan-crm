package com.loan.cps.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.loan.cps.entity.LenderPO;
import com.loan.cps.entity.Session;

public class LenderFilter {
	
	public static List<LenderPO> filter(Integer quota,Session session,List<LenderPO> cacheList,int size){
		List<LenderPO> keySet = cacheList.stream().filter(po->{
			Set<String> sendFristSet = session.getSendFristSet();
			if(sendFristSet==null) {
				return true;
			}
			return !sendFristSet.contains(po.getLenderId());
		}).sorted(Comparator.comparing((LenderPO po) -> {
			return Math.abs(po.getSettlement()-quota);
		})).collect(Collectors.toList());
		if(keySet==null||keySet.isEmpty()) {
			return new ArrayList<>();
		}
		List<LenderPO> result = new ArrayList<>();;
		for(LenderPO s:keySet) {
			if(result.size()<size) {
				result.add(s);
			}
		}
		session.getSendFristSet().addAll(result.stream().collect(Collectors.groupingBy(LenderPO::getLenderId)).keySet());
		return result;
	}
	
	public static List<LenderPO> filter2(Integer quota,Session session,List<LenderPO> cacheList,int size){
		List<LenderPO> keySet = cacheList.stream().sorted(Comparator.comparing((LenderPO po) -> {
			return Math.abs(po.getSettlement()-quota);
		})).collect(Collectors.toList());
		if(keySet==null||keySet.isEmpty()) {
			return new ArrayList<>();
		}
		List<LenderPO> result = new ArrayList<>();;
		for(LenderPO s:keySet) {
			if(result.size()<size) {
				result.add(s);
			}
		}
		session.getSendFristSet().addAll(result.stream().collect(Collectors.groupingBy(LenderPO::getLenderId)).keySet());
		return result;
	}
}
