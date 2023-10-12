package com.daofen.crm.service.customer;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.config.AppContextUtil;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.counselor.dao.CounselorMapper;
import com.daofen.crm.service.counselor.model.CompanyCounselorPO;
import com.daofen.crm.service.customer.dao.CustomerMapper;
import com.daofen.crm.service.customer.dao.MemorandumMapper;
import com.daofen.crm.service.customer.model.CustomerPO;
import com.daofen.crm.service.customer.model.MemorandumPO;

@Component
public class IMemorandumService implements MemorandumService{
	
	private static final Logger LOG = LoggerFactory.getLogger(ICustomerFacade.class);
	
	@Autowired
	private MemorandumMapper memorandumMapper;
	
	@Autowired
	private CustomerMapper customerMapper;
	
	@Autowired
	private CounselorMapper counselorMapper;

	@Override
	public MemorandumPO get(Long custId) {
		return memorandumMapper.get(custId);
	}

	@Override
	public void add(MemorandumPO po) {
		if(po.getThingTime().before(new Date())) {
			throw new CrmException("000001", "日期错误");
		}
		if(StringUtils.isEmpty(po.getContent())) {
			throw new CrmException("000001", "备忘录内容不能为空");
		}
		if(po.getContent().length()>=50) {
			throw new CrmException("000001", "备忘录内容不能超50个字");
		}
		if(po.getCustId()==null) {
			throw new CrmException("000001", "客户ID不能为空");
		}
		po.setState(0);
		memorandumMapper.add(po);
	}

	@Override
	public void del(Long id) {
		memorandumMapper.del(id);
	}

	@Override
	public void update(MemorandumPO po) {
		memorandumMapper.update(po);
	}

	@Override
	public List<MemorandumPO> getList() {
		return memorandumMapper.getList();
	}
	
	@Scheduled(fixedRate = 2*60*1000)
	private void trigger() {
		List<MemorandumPO> list = getList();
		if(list==null||list.isEmpty()) {
			LOG.info("无触发,备忘录定时任务结束");
			return ;
		}
		for(MemorandumPO po:list) {
			PageVO<CustomerPO> vo = new PageVO<CustomerPO>();
			CustomerPO custP = new CustomerPO();
			custP.setId(po.getCustId());
			vo.setParam(custP);
			List<CustomerPO> customerList = customerMapper.getCustomerList(vo);
			if(customerList==null||customerList.size()==0) {
				LOG.info("备忘录定时提醒  客户不存在 custid=" +po.getCustId());
				po.setState(1);
				memorandumMapper.update(po);
				continue;
			}
			CustomerPO customerPO = customerList.get(0);
			Long businessId = customerPO.getBusinessId();
			if(businessId==0) {
				LOG.info("备忘录定时提醒  无所属顾问");
				po.setState(1);
				memorandumMapper.update(po);
				continue;
			}
			CompanyCounselorPO coun = counselorMapper.selectCounselorById(businessId);
			Set<String> keys = AppContextUtil.getBean(StringRedisTemplate.class).keys(LoginUtil.LOGIN_PREFIX+coun.getCompanyId()+"_"+coun.getMobile()+"_"+"*");
			if(keys==null || keys.size()==0) {
				LOG.info("备忘录定时提醒  顾问未登陆");
				po.setState(1);
				memorandumMapper.update(po);
				continue;
			}
			String next = keys.iterator().next();
			ServerEventListener.sendMemorandum(next, po, customerPO);
			po.setState(1);
			memorandumMapper.update(po);
			LOG.info("备忘录定时提醒 user=" +next+" cust = "+customerPO.getId());
		}
		LOG.info("备忘录定时任务结束");
	}
}
