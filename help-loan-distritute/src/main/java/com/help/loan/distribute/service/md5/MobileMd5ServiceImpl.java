package com.help.loan.distribute.service.md5;

import com.help.loan.distribute.service.md5.dao.MobileMd5Dao;
import com.help.loan.distribute.service.md5.model.MobileMd5PO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MobileMd5ServiceImpl implements MobileMd5Service{

    @Autowired
    private MobileMd5Dao mobileMd5Dao;


    @Override
    public void addMobileMd5(MobileMd5PO po) {
        if(null == po)
            return;

        if(StringUtils.isBlank(po.getCity()) || StringUtils.isBlank(po.getMd5()))
            return;

        MobileMd5PO old = mobileMd5Dao.selectByCityAndMd5(po);
        if(null == old){
            mobileMd5Dao.insertMobileMd5(po);
        }
    }
}
