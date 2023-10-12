package com.loan.cps.common;

import java.util.List;

import com.loan.cps.entity.LenderPO;

public class LenderMsgBuilder {
	
	public static final byte FINISH = 101;
	
	public static final byte FIVE_MIN = 102;
	
	public static final byte ONE_HOUS = 103;
	
	public static final byte THREE_HOUS = 104;

	private static final String LENDER_APPLY_URL = "\uD83D\uDC49<a href=\"http://%s/dialogue/lender/apply?uuid=%s&lenderId=%s&location=%s\">%s</a>\n\n";
	
	private static final String WEHCAT_MENU_TEXT_MSG_MORE = "查看更多，<a href=\"weixin://bizmsgmenu?msgmenucontent=换一批&msgmenuid=19999\">换一批</a>";
	
	public static String buildLenderMsg(List<LenderPO> lenders,String userId,String domain2,byte location) {
		StringBuilder builder = new StringBuilder();
		for(LenderPO s:lenders) {
			builder.append(String.format(LENDER_APPLY_URL, domain2,userId,s.getLenderId(),location,s.getName()));
		}
		builder.append(WEHCAT_MENU_TEXT_MSG_MORE);
		return builder.toString();
	}
	
	public static String buildLenderMsg2(List<LenderPO> lenders,String userId,String domain2,byte location) {
		StringBuilder builder = new StringBuilder();
		for(LenderPO s:lenders) {
			builder.append(String.format(LENDER_APPLY_URL, domain2,userId,s.getLenderId(),location,s.getName()));
		}
		return builder.toString();
	}
	
}
