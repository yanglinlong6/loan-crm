package com.daofen.crm.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.service.order.dao.BankMapper;
import com.daofen.crm.service.order.model.BankPO;

@Component
public class IBankService implements BankService{

	@Autowired
	private BankMapper bankMapper;
	
	@Override
	public PageVO<BankPO> getBankList(PageVO<BankPO> vo) {
		vo.setData(bankMapper.getBankList(vo));
		vo.setTotalCount(bankMapper.getBankListCount(vo));
		return vo;
	}

	@Override
	public void addBank(BankPO po) {
		bankMapper.addBank(po);
	}

	@Override
	public void delBank(Long id) {
		bankMapper.delBank(id);
	}

}
