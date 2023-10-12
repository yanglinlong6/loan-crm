package com.help.loan.distribute.service.api.dao;

import com.help.loan.distribute.service.api.model.DispatcheRecPO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Mapper
public interface DispatcheRecDao {

    @Transactional
    void add(DispatcheRecPO po);

}
