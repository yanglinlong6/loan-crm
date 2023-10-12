package com.loan.cps.dao;


import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;
import com.loan.cps.entity.LenderPO;

import java.util.List;

/**
 *
 * 产品模块数据库映射类
 * @auth 张秋平 2019-07-31
 */
@Mapper
public interface LenderDao {


    /**
     * 根据产品id查询产品信息
     * @param lenderId 产品id
     * @return LenderPO
     * @auth zhangqiuping 2019-07-31
     */
	LenderPO selectByLenderId(String lenderId);

    /**
     * 根据机构id查询产品信息
     * @param companyId 机构id
     * @return List<LenderPO>
     * @auth zhangqiuping 2019-07-31
     */
    List<LenderPO> selectByCompanyId(Long companyId);

    /**
     * 查询所有产品列表：排序下线状态的产品
     * @return  List<LenderPO>
     * @auth zhangqiuping 2019-07-31
     */
    List<LenderPO> selectAll();
    /**
     * 查询在库机构ID
     * @param params
     * @return
     */
    List<Long> selExclude(String mobile);
    /**
     *	根据产品名称模糊查询产品
     * @param params
     * @return
     */
    List<LenderPO> selByName(String name);
    /**
     * 查询对应用户等级下的产品
     * @return  List<LenderPO>
     * @auth zhangqiuping 2019-07-31
     */
    List<LenderPO> selByUser(JSONObject obj);
    /**
     * 查询用户未申请产品
     * @return  List<LenderPO>
     * @auth zhangqiuping 2019-07-31
     */
    List<LenderPO> selByNoApplyList(String userId);

}