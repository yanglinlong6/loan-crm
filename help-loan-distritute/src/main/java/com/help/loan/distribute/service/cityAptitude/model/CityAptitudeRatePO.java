package com.help.loan.distribute.service.cityAptitude.model;

import com.help.loan.distribute.common.BasePO;
import com.help.loan.distribute.common.utils.JSONUtil;

public class CityAptitudeRatePO extends BasePO {

    private String currentDate;
    private String city;
    private Double publicFund;
    private Double house;
    private Double car;
    private Double income;
    private Double insurance;
    private Double company;
    private Double noAptitude;
    private Double publicFundIncome;

    private Double ordinary;//一般客户占比：<0.05
    private Double well;// 良好客户占比 0.05-0.103
    private Double excellent;//优秀客户占比 0.103-0.144
    private Double importance;//重要客户占比 0.144
    private Integer totalSucCount;
    private Integer highScoreCustomerCount;//高分客户数量

//    public CityAptitudeRatePO(String currentDate, String city, Double publicFund, Double house, Double car, Double income,Double insurance, Double company, Double noAptitude) {
//        this.currentDate = currentDate;
//        this.city = city;
//        this.publicFund = publicFund;
//        this.house = house;
//        this.car = car;
//        this.income = income;
//        this.company = company;
//        this.noAptitude = noAptitude;
//    }

    public CityAptitudeRatePO() {
    }

    public Integer getTotalSucCount() {
        return totalSucCount;
    }

    public void setTotalSucCount(Integer totalSucCount) {
        this.totalSucCount = totalSucCount;
    }

    public Integer getHighScoreCustomerCount() {
        return highScoreCustomerCount;
    }
    public void setHighScoreCustomerCount(Integer highScoreCustomerCount) {
        this.highScoreCustomerCount = highScoreCustomerCount;
    }
    public Double getOrdinary() {
        return ordinary;
    }
    public void setOrdinary(Double ordinary) {
        this.ordinary = ordinary;
    }
    public Double getWell() {
        return well;
    }
    public void setWell(Double well) {
        this.well = well;
    }
    public Double getExcellent() {
        return excellent;
    }
    public void setExcellent(Double excellent) {
        this.excellent = excellent;
    }
    public Double getImportance() {
        return importance;
    }
    public void setImportance(Double importance) {
        this.importance = importance;
    }
    public Double getPublicFundIncome() {
        return publicFundIncome;
    }

    public void setPublicFundIncome(Double publicFundIncome) {
        this.publicFundIncome = publicFundIncome;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate == null ? null : currentDate.trim();
    }

    public Double getInsurance() {
        return insurance;
    }

    public void setInsurance(Double insurance) {
        this.insurance = insurance;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }

    public Double getPublicFund() {
        return publicFund;
    }

    public void setPublicFund(Double publicFund) {
        this.publicFund = publicFund;
    }

    public Double getHouse() {
        return house;
    }

    public void setHouse(Double house) {
        this.house = house;
    }

    public Double getCar() {
        return car;
    }

    public void setCar(Double car) {
        this.car = car;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getCompany() {
        return company;
    }

    public void setCompany(Double company) {
        this.company = company;
    }

    public Double getNoAptitude() {
        return noAptitude;
    }

    public void setNoAptitude(Double noAptitude) {
        this.noAptitude = noAptitude;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}