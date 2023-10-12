package com.crm.common;

public interface CrmConstant {
    /**INIT=0 表示初始状态*/
    byte INIT = 0;

    /**YES=1 标识是或者成功*/
    byte YES = 1;

    String ALL_CITY = "全国";

    /**
     * 创建者
     */
    interface CreateBy{


        String IMPORT = "import";

        /**
         * 第一次分配dis
         */
        String DIS = "dis";

        /**
         * 再分配again
         */
        String AGAIN = "again";

        /**
         * 给客户发送短信表示:customer
         */
        String CUSTOMER = "customer";
    }

    /**
     * 返回结果常量
     */
    interface ResultCode{

        /**200-成功*/
        int SUCCESS = 200;

        /** 500-异常 */
        int EX = 500;

        /** 600-失败 */
        int FAIL = 600;

        /**未登陆授权*/
        int NOT_AUTH = 401;

        /**没有权限*/
        int NOT_PERMISSION = 402;

        /**api客户导入,已存在:601*/
        int EXIST = 601;

    }

    interface Org{

        interface Status{
            byte START = 1;
            byte STOP = 0;
        }


        interface Auto{
            byte Y = 1;
            byte N = 0;
        }

        interface Dial{
            byte Y = 1;
            byte N = 0;

        }
    }

    interface BehindCustomer{

        interface Process{
            byte wait = 0;
            /**已分配*/
            byte ASSIGND = 1;

            byte PROCESS = 2;

            byte COMPLETE = 3;
        }


    }
    /**产品常量*/
    interface Product{

        /**产品跟进进度: 产品处理状态:0-新建，1-处理中，2-完成*/
        interface Process{
            /**0-新建*/
            byte INIT = 0;

            /**1-处理中*/
            byte PRO = 1;

            /**2-完成*/
            byte COMPLETE = 2;
        }

    }

    /**门店常量*/
    interface Shop{

        /**门店类型: 1-前端,2-后端*/
        interface Type{
            byte QIAN = 1;
            byte HOU = 2;
        }

    }



    interface Date{
        String START = " 00:00:00";
        String END = " 23:59:59";
    }

    /** 角色常量  */
    interface Role{

        long ADMIN = 0;

        String ADMIN_NAME = "管理员";

        /**角色类型：0-总经理(管理员)，1-门店管理者，2-团队管理者，3-普通*/
        interface  Type{
            /**0-总经理(管理员)*/
            byte ADMIN = 0;

            /**1-门店管理者*/
            byte SHOP = 1;

            /**2-团队管理者*/
            byte TEAM = 2;

            /**3-普通*/
            byte COMMON = 3;

            /**
             * 4-渠道
             */
            byte CHANNEL = 4;
        }

    }


    /**客户常量*/
    interface Customer{

        Byte ALL = 1;

        String init = "0";

        /**星级常量：0,1,2,2.5,3,4  对应不同的星级*/
        interface Level{
            /**0星*/
            String ZERO = "0";

            /**1星*/
            String ONE = "1";

            /**2星*/
            String TWO = "2";

            /**2.5星*/
            String TWO_FIVE = "2.5";

            /**3星*/
            String THREE = "3";

            /**4星*/
            String FOUR = "4";

            /**5星*/
            String FIVE = "5";
        }

        /**有效客户 或者 无效客户标识：是否符产品需求：0-初始状态(默认)，1-符合，2-不符合*/
        interface Fit{
            byte INIT = 0;
            byte FIT = 1;
            byte NOT_FIT = 2;
        }

        /**是否是再次分配客户*/
        interface Again{

            /**1-是*/
            byte TRUE = 1;

            /**0-不是*/
            byte FALSE = 0;

        }

        /**
         * 公共池常量：0-否，1-是
         */
        interface PublicPool{
            byte Y = 1;
            byte N = 0;
        }

        /**
         * 是否自建客户
         */
        interface Zijian{

            byte YES = 1;

            byte NO = 0;

        }

        /**客户接通装填：通话状态：0-默认初始状态,新客户未联系，1-未接，2-关机，3-拒接，4-通话中，5-外地，6-已接通*/
        interface Call{
            /**0-默认初始状态*/
            byte INIT = 0;

            /**1-未接*/
            byte NOT_CALL = 1;

            /**2-关机*/
            byte CLOSE = 2;

            /**3-拒接*/
            byte JU_JUE = 3;

            /**4-通话中*/
            byte IN_CALL = 4;

            /**5-外地*/
            byte NOT_CITY = 5;

            /**6-已接通*/
            byte CALL = 6;
        }

        /** 客户进度*/
        interface Progress{
            /**新客户，待分配*/
            byte INIT = 0;

            /**已分配*/
            byte IS_DIS = 1;

            byte ING = 2;

            /**
             * 已签约
             */
            byte SIGN = 4;
        }

        /**分配类型：0-新数据分配，1-协助，2-再分配*/
        interface DisStatus{

            byte NEW = 0;

            byte HELP = 1;

            byte AGEIN = 2;

        }

        /**是否是标记为:第三方产品客户:0-否,1-是*/
        interface ThirdParty{
            /**1-是第三方产品客户*/
            byte Y = 1;
            /**0-否*/
            byte N = 0;
        }


    }

    interface Employee{

        /**员工状态：1-在职，0-离职*/
        interface Status{

            /**在职：1*/
            byte YES = 1;

            /**离职：0*/
            byte NO = 0;

        }

        /**是否接收数据：1-接收，0-不接收（默认）*/
        interface Receive{

            /**1-接收*/
            byte YES = 1;

            /**0-不接收*/
            byte NO = 0;

        }

        interface Login{

            byte Y = 1;
            byte N = 0;

        }

        /**员工消息*/
        interface Msg{
            interface Status{
                /**0-新消息*/
                byte NEW = 0;
                /**1-已发送，未读*/
                byte SEND = 1;
                /**2-已读*/
                byte READ = 2;
            }
        }
    }




    /**签约合同*/
    interface Contract{
        /**签约状态: 0-初始, 1-已签约, 2-收款中, 3-收款完成,4-完件*/
        interface  State{
            /**：0-初始*/
            Byte INIT = 0;

            /**1-已签约*/
            Byte YES = 1;

            /**2-收款中*/
            Byte INCOMING = 2;

            /**收款完成*/
            Byte INCOME = 3;

            /**完件*/
            Byte COMPLATE = 4;
        }

        interface Doc{

            byte YES = 1;

            byte NO = 0;

        }
    }

    /***
     * 进件常量
     */
    interface Import{

        /**状态*/
        interface State{
            byte init = 0;

            byte INCOME=5;

            byte COMPLAE= 6;
        }
    }

    /**
     * 开关
     */
    interface Switch{
        String KEY = "org_switch_state";

        /**OFF-关*/
        byte OFF = 0;

        /**NO = 开*/
        byte NO = 1;
    }

    interface Config {

        /** 机构媒体配置key，hash类型*/
        String MEDIA = "media";

        /** 机构媒体配置key，hash类型  城市配置*/
        String CITY = "city";

        /**客户状态缓存配置redis的key*/
        String CUSTOMER_STATUS_KEY = "customer_status";

        interface City{
            String CITY_KEY = "city_list_";
        }

        interface Customer{

            /**客户状态缓存Field*/
            String FIELD = "customer_status";

        }

        interface ESign{

            String FIELD = "esign";

            /**e钱包缓存配置*/
            interface Key {

                String ACCOUNT = "account_id";

                String APPID = "appid";

                String SECRET = "secret";

                String DOMAIN = "domain";

                String ORG_ID = "4d1fdae90fae463098a1cb568d409474";

                String FILE_DIR = "filepath";

            }

            /**redis保存e签宝token:key*/
            String TOKEN = "esign_token";

            /**redis保存e签宝刷新token:key*/
            String TOKEN_REFRESH = "esign_token_refresh";



        }

        /**ssh redis缓存key*/
        interface Upload{

            String UPLOAD = "upload";
            /**合同上传保存的目录*/
            String SOURCE_DIC = "source_dic";
            /**合同文件远程复制ip,用户名,密码*/
            String SCP_TARGET = "scp_target";
            /**合同远程保存的目录*/
            String SCP_TARGET_DIC = "scp_target_dir";

            String DOMAIN2 = "domain2";
        }

        /**
         * 机构新客户分配redis缓存key常量
         */
        interface Distribute{

            String DIS = "distribute_";

            String DIS_EMPLOYEE = "distribute_employee_";
        }



        interface Socket{

            String WEB_SOCKET_COUNTS = "web_socket_counts";

            String WEB_SOCKET_USERS = "web_socket_users";

        }

    }



}
