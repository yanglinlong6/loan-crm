package com.crm.service.label.dao;


import com.crm.service.label.model.LabelPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface LabelMapper {

    int insertLabel(LabelPO record);

    int deleteLabel(Long id);

    List<LabelPO> selectAllLabel(Long orgId);


    LabelPO selectLabel(@Param("orgId") Long orgId,@Param("name") String name);




}