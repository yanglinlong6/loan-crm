package com.loan.cps.common;

import java.util.regex.Pattern;

public class NumberUtils {
	
	public static void main(String[] args) {
 
        int number = NumberUtils.getIntegerByNumberStr("1");
        System.out.println(number);
 
        number = NumberUtils.getIntegerByNumberStr("1");
        System.out.println(number);
    }
 
    /**
     * 支持到12位
     *
     * @param numberStr 中文数字
     * @return int 数字
     */
    public static int getIntegerByNumberStr(String numberStr) {
 
        // 返回结果
        int sum = 0;
 
        // null或空串直接返回
        if (numberStr == null || ("").equals(numberStr)) {
            return sum;
        }
 
        // 过亿的数字处理
        if (numberStr.indexOf("亿") > 0) {
            String currentNumberStr = numberStr.substring(0, numberStr.indexOf("亿"));
            int currentNumber = testA(currentNumberStr);
            sum += currentNumber * Math.pow(10, 8);
            numberStr = numberStr.substring(numberStr.indexOf("亿") + 1);
            if(numberStr.length()==1&&Pattern.matches("[0-9]*", numberStr)) {
            	sum += Integer.valueOf(numberStr) * Math.pow(10, 7);
            	numberStr = "0";
            }
            if(numberStr.length()==1&&Pattern.matches("[一二三四五六七八九]*", numberStr)) {
            	sum += testB(numberStr) * Math.pow(10, 7);
            	numberStr = "0";
            }
        }
 
        // 过万的数字处理
        if (numberStr.indexOf("万") > 0) {
            String currentNumberStr = numberStr.substring(0, numberStr.indexOf("万"));
            int currentNumber = testA(currentNumberStr);
            sum += currentNumber * Math.pow(10, 4);
            numberStr = numberStr.substring(numberStr.indexOf("万") + 1);
            if(numberStr.length()==1&&Pattern.matches("[0-9]*", numberStr)) {
            	sum += Integer.valueOf(numberStr) * Math.pow(10, 3);
            	numberStr = "0";
            }
            if(numberStr.length()==1&&Pattern.matches("[一二三四五六七八九]*", numberStr)) {
            	sum += testB(numberStr) * Math.pow(10, 3);
            	numberStr = "0";
            }
        }
 
        // 小于万的数字处理
        if (!("").equals(numberStr)) {
            int currentNumber = testA(numberStr);
            sum += currentNumber;
        }
 
        return sum;
    }
 
    /**
     * 把亿、万分开每4位一个单元，解析并获取到数据
     * @param testNumber
     * @return
     */
    public static int testA(String testNumber) {
        // 返回结果
        int sum = 0;
 
        // null或空串直接返回
        if(testNumber == null || ("").equals(testNumber)){
            return sum;
        }
        
        if(Pattern.matches("[0-9]*", testNumber)) {
        	return Integer.valueOf(testNumber);
        }
        
        // 获取到千位数
        if (testNumber.indexOf("千") > 0) {
            String currentNumberStr = testNumber.substring(0, testNumber.indexOf("千"));
            int a = 0;
            if(Pattern.matches("[0-9]*", currentNumberStr)) {
            	a =  Integer.valueOf(currentNumberStr);
            }else {
            	a = testB(currentNumberStr);
            }
            sum += a * Math.pow(10, 3);
            testNumber = testNumber.substring(testNumber.indexOf("千") + 1);
            if(testNumber.length()==1&&Pattern.matches("[0-9]*", testNumber)) {
            	sum += Integer.valueOf(testNumber) * Math.pow(10, 2);
            	testNumber = "0";
            }
            if(testNumber.length()==1&&Pattern.matches("[一二三四五六七八九]*", testNumber)) {
            	sum += testB(testNumber) * Math.pow(10, 2);
            	testNumber = "0";
            }
        }
 
        // 获取到百位数
        if (testNumber.indexOf("百") > 0) {
            String currentNumberStr = testNumber.substring(0, testNumber.indexOf("百"));
            int a = 0;
            if(Pattern.matches("[0-9]*", currentNumberStr)) {
            	a =  Integer.valueOf(currentNumberStr);
            }else {
            	a = testB(currentNumberStr);
            }
            sum += a * Math.pow(10, 2);
            testNumber = testNumber.substring(testNumber.indexOf("百") + 1);
            if(testNumber.length()==1&&Pattern.matches("[0-9]*", testNumber)) {
            	sum += Integer.valueOf(testNumber) * Math.pow(10, 1);
            	testNumber = "0";
            }
            if(testNumber.length()==1&&Pattern.matches("[一二三四五六七八九]*", testNumber)) {
            	sum += testB(testNumber) * Math.pow(10, 1);
            	testNumber = "0";
            }
        }
 
        // 对于特殊情况处理 比如10-19是个数字，十五转化为一十五，然后再进行处理
        if (testNumber.indexOf("十") == 0) {
            testNumber = "一" + testNumber;
        }
 
        // 获取到十位数
        if (testNumber.indexOf("十") > 0) {
            String currentNumberStr = testNumber.substring(0, testNumber.indexOf("十"));
            int a = 0;
            if(Pattern.matches("[0-9]*", currentNumberStr)) {
            	a =  Integer.valueOf(currentNumberStr);
            }else {
            	a = testB(currentNumberStr);
            }
            sum += a * Math.pow(10, 1);
            testNumber = testNumber.substring(testNumber.indexOf("十") + 1);
        }
 
        // 获取到个位数
        if(!("").equals(testNumber)){
        	int a = 0;
            if(Pattern.matches("[0-9]*", testNumber)) {
            	a =  Integer.valueOf(testNumber);
            }else {
            	a = testB(testNumber.replaceAll("零",""));
            }
            sum += a;
        }
 
        return sum;
    }
 
    public static int testB(String replaceNumber) {
        switch (replaceNumber) {
            case "一":
                return 1;
            case "二":
                return 2;
            case "三":
                return 3;
            case "四":
                return 4;
            case "五":
                return 5;
            case "六":
                return 6;
            case "七":
                return 7;
            case "八":
                return 8;
            case "九":
                return 9;
            case "零":
                return 0;
            default:
                return 0;
        }
    }

}
