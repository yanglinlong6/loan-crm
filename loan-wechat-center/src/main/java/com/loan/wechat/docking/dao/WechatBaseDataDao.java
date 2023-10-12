package com.loan.wechat.docking.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.loan.wechat.docking.entity.WechatDTO;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface WechatBaseDataDao {
	
	List<WechatDTO> getAllWechatsList();
	
}
