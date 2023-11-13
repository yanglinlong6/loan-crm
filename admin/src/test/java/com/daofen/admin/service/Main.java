package com.daofen.admin.service;

public class Main {
  public static void main(String[] args) {
    int hour = 01;
    int minute = 03;
    int convertToMinutes = convertToMinutes(hour, minute);
    System.out.println(convertToMinutes);
  }

  public static int convertToMinutes(int hour, int minute) {
    int totalMinutes = hour * 60 + minute;
    return totalMinutes;
  }
}
