package com.help.loan.distribute.service.lender;

import com.help.loan.distribute.service.lender.model.LenderPO;

import java.util.List;

public class LenderMsgBuilder {

    private static final String LENDER_APPLY_URL = "\uD83D\uDC49<a href=\"http://%s/dialogue/lender/apply?uuid=%s&lenderId=%s\">%s</a>\n\n";

    private static final String WEHCAT_MENU_TEXT_MSG_MORE = "查看更多，<a href=\"weixin://bizmsgmenu?msgmenucontent=换一批&msgmenuid=19999\">换一批</a>";

    public static String buildLenderMsg(List<LenderPO> lenders, String userId, String domain2) {
        StringBuilder builder = new StringBuilder();
        for(LenderPO s : lenders) {
            builder.append(String.format(LENDER_APPLY_URL, domain2, userId, s.getLenderId(), s.getName()));
        }
        builder.append(WEHCAT_MENU_TEXT_MSG_MORE);
        return builder.toString();
    }

}
