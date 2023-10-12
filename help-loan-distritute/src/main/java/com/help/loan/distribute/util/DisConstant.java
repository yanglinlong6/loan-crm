package com.help.loan.distribute.util;

/**
 * 常量
 */
public interface DisConstant {


    /**
     * 机构配量：常量
     */
    interface OrgApt{

        /**
         * 机构配量类型：0-信贷，1-房抵贷，2-车抵贷
         */
        interface Type{
            /**信贷*/
            Byte WEB = 0;

            Byte HOUSE = 1;

            Byte CAR = 2;

            /**信用卡逾期*/
            Byte CREDIT = 3;
        }
    }

    interface User{
        interface Type{
            String WEB = "web";
            String CAR = "car";
            String HOUSE = "house";
            String CREDIT = "credit";
        }

    }


}
