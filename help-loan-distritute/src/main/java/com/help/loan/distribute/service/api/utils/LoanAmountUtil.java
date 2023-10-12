package com.help.loan.distribute.service.api.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class LoanAmountUtil {
    public static Integer transform(String loanAmount) {
        Integer amount = Integer.valueOf(50000);
        if(Pattern.matches("\\d+", loanAmount)) {
            amount = Integer.valueOf(loanAmount);
            if(amount <50000)
                return 50000;
        } else if("《30-50万》".equals(loanAmount) || "50万".equals(loanAmount) || "20万以上".equals(loanAmount)) {
            amount = Integer.valueOf(500000);
        } else if("10-20万".equals(loanAmount) || "《10-30万》".equals(loanAmount) || "20万".equals(loanAmount)){
            amount = Integer.valueOf(200000);
        }else if("15万".equals(loanAmount)){
            amount = 150000;
        }else if("10万".equals(loanAmount)) {
            amount = Integer.valueOf(100000);
        }else if("《3-10万》".equals(loanAmount)  || "5-10万".equals(loanAmount)  || "《5-10万》".equals(loanAmount)){
            amount = 100000;
        }else if("《3-5万》".equals(loanAmount) || "3-5万".equals(loanAmount) || "5万".equals(loanAmount)) {
            amount = Integer.valueOf(50000);
        } else if("《3万以下》".equals(loanAmount)) {
            amount = Integer.valueOf(50000);
        } else {
            amount = Integer.valueOf(50000);
        }
        return amount;
    }

    public static String transformToWan(String loanAmount) {
        String amount = "5";
        try{
            if(StringUtils.isBlank(loanAmount)) {
                return amount;
            } else if(Pattern.matches("\\d+", loanAmount)) {
                Integer value = Integer.valueOf(loanAmount);
                if(value < 5){
                    return amount;
                }
                if(value >= 5 && value < 10000){
                    return value.toString();
                }
                if(value >=10000 && value < 50000){
                    return amount;
                }
                return String.valueOf(Integer.valueOf(loanAmount).intValue() / 10000);
            } else if("《30-50万》".equals(loanAmount) || "50万".equals(loanAmount) || "20万以上".equals(loanAmount)) {
                return "50";
            } else if("《10-30万》".equals(loanAmount) || "20万".equals(loanAmount) || "10-20万".equals(loanAmount)) {
                return "20";
            }else if("15万".equals(loanAmount)){
                amount = "15";
            } else if("《5-10万》".equals(loanAmount) || "5-10万".equals(loanAmount) ) {
                return "10";
            }else if("《3-10万》".equals(loanAmount) || "10万".equals(loanAmount)){
                return "10";
            } else if("《3-5万》".equals(loanAmount) || "3-5万".equals(loanAmount) || "5万".equals(loanAmount)) {
                return amount;
            }else if("《3万以下》".equals(loanAmount)) {
                return amount;
            }
            return amount;
        }catch (Exception e){
            return "5";
        }
    }
}
