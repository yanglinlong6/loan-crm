/**
 * 
 */
package com.loan.cps.common;

import java.util.ArrayList;
import java.util.List;

import com.loan.cps.entity.LenderPO;

/**
 * @author KZM
 *
 */
public class LenderCache {

	private static List<LenderPO> cacheList= new ArrayList<>();
	
	private static LenderPO dfLender;

	public static List<LenderPO> getCacheList() {
		return cacheList;
	}

	public static void setCacheList(List<LenderPO> cacheList) {
		LenderCache.cacheList = cacheList;
	}

	public static LenderPO getDfLender() {
		return dfLender;
	}

	public static void setDfLender(LenderPO dfLender) {
		LenderCache.dfLender = dfLender;
	}
	
}
