package com.loan.cps.matrix;

import com.loan.cps.common.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class MemorandumPO extends BasePO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long custId;
	
	private Integer state;
	
	private Date thingTime;
	
	private String content;

}
