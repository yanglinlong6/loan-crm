package com.daofen.admin.service;

import java.util.Date;

import com.daofen.admin.utils.DateUtil;

public class Main {
  public static void main(String[] args) {
    int hour = 01;
    int minute = 03;
    int convertToMinutes = convertToMinutes(hour, minute);
    System.out.println(convertToMinutes);

    String line = "8:30";
    System.out.println(line.split("[:：]")[0]);
    System.out.println(line.split("[:：]")[1]);
    System.out.println("8：30".split("[:：]")[0]);
    System.out.println("8：30".split("[:：]")[1]);

    int distributeTimeHour = Integer.valueOf(DateUtil.formatToString(new Date(), "HH")).intValue();
    System.out.println(distributeTimeHour);
    int distributeTimeMinute = Integer.valueOf(DateUtil.formatToString(new Date(), "mm")).intValue();
    System.out.println(distributeTimeMinute);
    int distributeTimeMinutes = DateUtil.convertToMinutes(distributeTimeHour, distributeTimeMinute);
    System.out.println(distributeTimeMinutes);
  }

  public static int convertToMinutes(int hour, int minute) {
    int totalMinutes = hour * 60 + minute;
    return totalMinutes;
  }
}
