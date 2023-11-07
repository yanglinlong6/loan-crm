package com.daofen.admin.service.user.model;

import lombok.Data;

import java.util.List;

/**
 * 用户关联组织VO实体类你
 */
@Data
public class LinkOrgVO {

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 组织ID
     */
    private List<Integer> orgIds;
}
