package com.loan.cps.service.cache.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CacheConfigPO{
    private Long id;

    private String field;

    private String key;

    private String value;


}