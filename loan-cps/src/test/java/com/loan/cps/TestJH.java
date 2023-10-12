package com.loan.cps;

import com.loan.cps.common.JSONUtil;
import com.loan.cps.common.MobileLocationUtil;

public class TestJH {

    public static void main(String[] args){
        System.out.println(JSONUtil.toString(MobileLocationUtil.getAndCheck("13632965527")));
    }
}
