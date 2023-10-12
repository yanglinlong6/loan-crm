package com.help.loan.distribute.service.md5.dao;

import com.help.loan.distribute.service.md5.model.MobileMd5PO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MobileMd5Dao {


    MobileMd5PO selectByCityAndMd5(MobileMd5PO po);

    int insertMobileMd5(MobileMd5PO po);

}
