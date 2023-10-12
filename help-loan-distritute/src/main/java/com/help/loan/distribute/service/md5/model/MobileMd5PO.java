package com.help.loan.distribute.service.md5.model;

import com.help.loan.distribute.common.utils.JSONUtil;
import lombok.Getter;
import lombok.Setter;



@Setter
@Getter
public class MobileMd5PO {

    private Long id;

    private String city;

    private String md5;

    private String orgName;

    public MobileMd5PO() {
    }

    public MobileMd5PO(String city, String md5) {
        this.city = city;
        this.md5 = md5;
    }

    public MobileMd5PO(String orgName,String city, String md5) {
        this.orgName = orgName;
        this.city = city;
        this.md5 = md5;
    }

    @Override
    public String toString() {
        if(null == this){
            return null;
        }
        return JSONUtil.toJsonString(this);
    }
}
