package com.help.loan.distribute.service.user.dao;


import com.alibaba.fastjson.JSONObject;
import com.help.loan.distribute.service.user.model.UserDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface WechatUserBindDao {

    UserDTO select(String userId);

    Integer selectCount(JSONObject o);
}
