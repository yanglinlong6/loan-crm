package com.daofen.crm.service.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.customer.dao.RemarkMapper;
import com.daofen.crm.service.customer.model.RemarkPO;

@Component
public class IRemarkService implements RemarkService{
	
	@Autowired
	private RemarkMapper remarkMapper;

	@Override
	public PageVO<RemarkPO> getRemarkList(PageVO<RemarkPO> vo) {
		vo.setData(remarkMapper.getRemarkList(vo));
		vo.setTotalCount(remarkMapper.getRemarkListCount(vo));
		return vo;
	}

	@Override
	public void addRemark(RemarkPO po) {
		remarkMapper.addRemark(po);
	}

	@Override
	public void delRemark(Long id) {
		remarkMapper.delRemark(id);
	}

}
