package com.help.loan.distribute.service.api.utils;

public class GenderUtil {

    public static String transform(Integer gender){
        String sex = "未知";
        if(gender == null) {
            sex = "未知";
        } else if(JudgeUtil.in(gender,1)) {
            sex = "男";
        } else if(JudgeUtil.in(gender,2)) {
            sex = "女";
        }
        return sex;
    }
}
