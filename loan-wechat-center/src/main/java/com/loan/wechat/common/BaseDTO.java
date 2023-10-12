package com.loan.wechat.common;

import java.io.Serializable;
import java.util.Date;


public class BaseDTO implements Cloneable,Serializable
{
	
	private static final long serialVersionUID = 1L;
	
	private String createdBy;
	
	private Date createdDate;
	
	private String updatedBy;
	
	private Date updatedDate;
	
	private String requestId;
	
	private String createBy;
	
	private Date createDate;
	
	private String updateBy;
	
	private Date updateDate;
	
	public Object clone()
	{
		try
		{
			return super.clone();
		} catch(CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	public String getCreatedBy()
	{
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}
	
	public Date getCreatedDate()
	{
		return createdDate;
	}
	
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}
	
	public String getUpdatedBy()
	{
		return updatedBy;
	}
	
	public void setUpdatedBy(String updatedBy)
	{
		this.updatedBy = updatedBy;
	}
	
	public Date getUpdatedDate()
	{
		return updatedDate;
	}
	
	public void setUpdatedDate(Date updatedDate)
	{
		this.updatedDate = updatedDate;
	}
	
	public String getRequestId()
	{
		return requestId;
	}
	
	public void setRequestId(String requestId)
	{
		this.requestId = requestId;
	}
	
	public String getCreateBy()
	{
		return createBy;
	}
	
	public void setCreateBy(String createBy)
	{
		this.createBy = createBy;
	}
	
	public Date getCreateDate()
	{
		return createDate;
	}
	
	public void setCreateDate(Date createDate)
	{
		this.createDate = createDate;
	}
	
	public String getUpdateBy()
	{
		return updateBy;
	}
	
	public void setUpdateBy(String updateBy)
	{
		this.updateBy = updateBy;
	}
	
	public Date getUpdateDate()
	{
		return updateDate;
	}
	
	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
	}
	
	public boolean isEmpty()
	{
		return false;
	}
	
}
