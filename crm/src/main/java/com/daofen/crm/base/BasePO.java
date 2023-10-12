package com.daofen.crm.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础对象
 *
 * @author zhangqiuping
 * @create_date 2018年9月1日 下午3:03:37
 * @since 2.2.4
 * <p>
 * 使用lombok工具：@Setter @Getter 自动生成get和set方法
 */
@Setter
@Getter
public class BasePO implements Serializable {

    private static final long serialVersionUID = -829752531601205940L;

    protected Long id;// 数据库自增id

    protected String createBy;// 创建人

    protected Date createDate;// 创建日期

    protected String updateBy;// 修改人

    protected Date updateDate;// 修改日期

    public String searchWord;// 修改日期

}
