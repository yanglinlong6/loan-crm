package com.help.loan.distribute.common.filter;

import com.alibaba.fastjson.JSON;
import com.help.loan.distribute.common.utils.WechatCenterUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 微信过滤表情
 *
 * @author hsw
 */
public class EmojiFilter {

    public static String filterEmoji(String source) {
        if(StringUtils.isBlank(source)) {
            return "公众号用户";
        }
        Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]|[a-zA-Z0-9\\-.`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]",
                Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
        Matcher emojiMatcher = emoji.matcher(source);
        if(emojiMatcher.find()) {
            source = emojiMatcher.replaceAll("");
            return source;
        }
        return source;
    }

    /**
     * 如果没有名字，则去公众号名字
     * @param source
     * @param userId
     * @return
     */
    public static String filterEmoji(String source,String userId) {
        try {
            String wechatName = JSON.parseObject(WechatCenterUtil.getWechat("","",userId)).getJSONObject("o").getString("wechat");
            if(StringUtils.isBlank(source)) {
                return wechatName+"用户";
            }
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]|[a-zA-Z0-9\\-.`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]",
                    Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if(emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("");
                if(source.length() ==1)
                    source = "姓"+source;
                return wechatName+"用户"+source;
            }
        }catch(Exception e){
            return "公众号用户";
        }
        return source;
    }

}
