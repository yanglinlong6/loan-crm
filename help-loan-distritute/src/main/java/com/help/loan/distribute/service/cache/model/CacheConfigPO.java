package com.help.loan.distribute.service.cache.model;

import com.help.loan.distribute.common.BasePO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CacheConfigPO extends BasePO {

    private String field;

    private String key;

    private String value;


}