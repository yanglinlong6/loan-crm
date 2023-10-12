package com.daofen.crm.base;

/**
 * crm系统常量对象
 */
public interface CrmConstant {

    /**机构常量*/
    interface Company{

        /**公司类型(1-总公司，2-分公司，3-加盟公司)*/
        interface Type{
            /**总公司：1*/
            byte PARENT = 1;

            /**分公司：2*/
            byte SUB = 2;

            /**加盟公司*/
            byte LEAGUE = 3;
        }

    }

    /**顾问模块常量*/
    interface Counselor{

        /**顾问状态(0-下线，1-上线）*/
        interface Status {
            /** 0-下线*/
            byte OFFLINE = 0;
            /** 1-上线*/
            byte ONLINE = 1;
        }

        /**是否开启接收(0-关闭，1-开启)*/
        interface Open {

            /** 1-开启*/
            byte OPEN = 1;

            /** 0-关闭*/
            byte CLOSE = 0;

        }

        /**顾问账户类型(1-内部，2-外部)*/
        interface Type {

            /**内部：1*/
            byte INNER = 1;

            /**外部账号：2*/
            byte OUT = 2;

        }


    }

    /**角色模块常量*/
    interface  Role{

        /**角色类型(0-超级管理员，1-管理员，2-门店店长，3-团队主管，4-普通账号)*/
        /**0-超级管理员，1-管理员，2-公司管理，3-门店主管，4-团队主管，5-普通账号*/
        interface Type{
            /**超级管理员：0*/
            byte SUPER = 0;

            /**1-管理员*/
            byte ADMIN = 1;

            /**2-公司管理员*/
            byte COMPANY = 2;

            /**2-门店店长*/
            byte SHOP = 3;

            /**3-团队主管*/
            byte GROUP = 4;

            /**4-普通账号*/
            byte COUNSELOR = 5;
        }

    }
    
    /**客户模块常量*/
    interface  Customer{

        /**数据状态(0-未分配，1-已分配，2-公共池，3-等待池 4-再分配)*/
        interface DataState{
            /**未分配：0*/
            int UNALLOCATED = 0;

            /**1-已分配*/
            int ALLOCATED = 1;

            /**2-公共池*/
            int PUBLIC_POOL = 2;

            /**3-等待池*/
            int WAIT_POOL = 3;
            
            /**4-再分配*/
            int AGAIN = 4;

        }
        
        /**流转类型：1自动分配 2再分配 3公共池分配 4等待池分配 5加入公共池*/
        interface CirculationState{

            /**1自动分配*/
            int AUTOMATIC = 1;

            /**2再分配*/
            int AGAIN = 2;

            /**3公共池分配*/
            int PUBLIC_POOL = 3;
            
            /**4等待池分配*/
            int WAIT_POOL = 4;
            
            /** 5加入公共池*/
            int JOIN_PUBLIC_POOL = 5;

        }

    }


    /**
     * 城市模块常量
     */
    interface City {


        /**
         * 城市模块：redis缓存key常量
         */
        interface Redis {

            /**
             * 城市组列表缓存前缀：city_list_
             */
            String CITY_KEY = "city_list_";

        }
    }
    
    /**
     * 媒体模块常量
     */
    interface Media {


        /**
         * APPID前缀
         */
        interface APPID_PREFIX {

            /**
             * 前缀
             */
            String PREFIX = "dolphin_";

        }
        
        /**
         * 媒体状态
         */
        interface State {

            /**
             * 开启
             */
            Integer OPEN = 0;
            /**
             * 关闭
             */
            Integer CLOSE = 1;

        }
    }
    
    /**
     * 媒体模块常量
     */
    interface Order {

        /**
         * 媒体状态
         */
        interface DataState {

            /**
             * 开启
             */
            Integer NORMAL = 0;
            /**
             * 关闭
             */
            Integer DELETE = 1;

        }
    }

}
