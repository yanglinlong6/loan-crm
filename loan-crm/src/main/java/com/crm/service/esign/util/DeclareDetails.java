package com.crm.service.esign.util;

/**
 * @description 声明明细
 * @author 宫清
 * @date 2019年7月21日 下午9:39:18
 * @since JDK1.7
 */
public class DeclareDetails {

	public static void showImportantMessage() {
		System.err.println("1、本Demo是用来阐述API接口基本使用方法,仅针对大众场景,供接口调用方(平台方)接入参考.特殊情况还请接口调用方(平台方)自行调整,确保符合接口调用方(平台方)实际业务需求;");
		System.err.println("2、如果是平台方自动签署，需要登录e签宝官网，查询平台印章ID");
		System.err.println("3、正式上线前需要提前2-4天联系e签宝工作人员进行接口权限和套餐次数的配置,请提前与e签宝工作人员进行沟通上线事宜;");
		System.err.println("4、其他事项请结合贵司实际业务需求和e签宝的接口文档进行联调,如有疑问请随时与e签宝工作人员进行交流确认;");
		System.err.println("- - - - - - - - - - # # # # - - - - - - - - - - \n");
	}
}
