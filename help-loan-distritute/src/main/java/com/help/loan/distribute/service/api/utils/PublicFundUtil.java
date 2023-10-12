package com.help.loan.distribute.service.api.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 公积金处理类
 */
public class PublicFundUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PublicFundUtil.class);

    public static int toInt(String publicFundString){
        if(StringUtils.isBlank(publicFundString)){
            return 0;
        }
        if("有，个人月缴800元以上".equals(publicFundString)){
            LOG.info("解析公积金：{}，解析后：{}",publicFundString,800);
            return 800;
        }
        if("有，个人月缴300-800元".equals(publicFundString)){
            LOG.info("解析公积金：{}，解析后：{}",publicFundString,500);
            return 500;
        }
        if("有，个人月缴300元以下".equals(publicFundString)){
            LOG.info("解析公积金：{}，解析后：{}",publicFundString,300);
            return 300;
        }
        if("没有公积金".equals(publicFundString)){
            LOG.info("解析公积金：{}，解析后：{}",publicFundString,0);
            return 0;
        }
        LOG.info("解析公积金：{}，解析后：{}",publicFundString,"最后为0");
        return 0;
    }


}
