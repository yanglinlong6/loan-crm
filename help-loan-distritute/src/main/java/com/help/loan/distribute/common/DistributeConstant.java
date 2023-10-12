package com.help.loan.distribute.common;

public interface DistributeConstant {

    interface Redis{

        interface Employee{
            String FIELD = "distribute_org_employee";
        }

    }

    /**
     * 结算记录常量
     */
    interface BillRec{

        /**结算状态: 0-未完成, 1-完成*/
        interface Status{

            /**1-完成*/
            byte ACCOMPLISH = 1;

            /**0-未完成*/
            byte NO_ACCOMPLISH = 0;

        }

    }


    interface LoanType{

        Byte CREDIT = 0;

        Byte HOUSE = 1;

        Byte CAR = 2;

        Byte FUND = 4;

    }

}
