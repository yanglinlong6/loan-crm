package com.crm;

import com.crm.common.CrmConstant;
import com.crm.service.esign.util.DefineException;
import org.apache.commons.lang3.StringUtils;

public class Test {

    private static Byte getProcess(String processList,String process){
        if(StringUtils.isBlank(processList) || StringUtils.isBlank(process)){
            return CrmConstant.Customer.Progress.SIGN;
        }
        String[] array = processList.split(",");
        for(String str : array){
            if(!str.contains(process)){
                continue;
            }
            return Byte.valueOf(str.split("-")[0]);
        }
        return CrmConstant.Customer.Progress.SIGN;
    }

    public static void main(String[] args) throws DefineException {
//        Byte process = getProcess("0-新客户,1-已分配,2-跟进中,3-有意向,4-已签约,5-进件,6-完成","已签约");
//        System.out.println(process);
//        TokenHelper.getTokenData(null);
//        SignHelper.startSignFlow("3f8d801245a4467eb0afcc409a8ce663");


        System.out.println(System.currentTimeMillis());
    }
}
