package com.loan.cps.common;

public interface CPSConstant {

    public interface Redis{

        public interface Advertising{
            String FIELD = "advertising";
            String CITY = "city";
            String CITY_MORTGAGE = "city_mortgage";

            String ONE_CITY = "advertising_one_city";
            String CITY_KEY = "city_list_";
        }
    }

    /**创建类型:LOAN-信贷,HOUSE-房抵,CAR-车抵,CREDIT-信用卡逾期*/
    interface CreateBy{

        /**信贷:web*/
        String LOAN = "web";

        /**房抵-house*/
        String HOUSE = "house";

        /**车抵-car*/
        String CAR = "car";

        /**信用卡逾期:credit*/
        String CREDIT = "credit";

        /**公积金:fund*/
        String Fund = "fund";

    }

    interface  ProductType{

        /**信贷:0*/
        Byte Credit =0;

        /**房抵-1*/
        Byte House = 1;

        /**车抵-2*/
        Byte Car = 2;

        /**信用卡逾期:3*/
        Byte Zhaiwu = 3;

        /**公积金:4*/
        Byte Fund = 4;

    }
}
