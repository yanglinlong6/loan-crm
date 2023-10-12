package com.daofen.crm.service.callAccount;

import com.daofen.crm.base.CrmException;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.service.callAccount.dao.CallAccountMapper;
import com.daofen.crm.service.callAccount.model.CallAccountPO;
import com.daofen.crm.utils.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CallAccountServiceImpl implements CallAccountService {

    @Autowired
    private CallAccountMapper callAccountMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    private static final String KEY = "call_account_key_";

    @Override
    public CallAccountPO getCallAccount(Long id) {
        String key = KEY+id;
        synchronized (key){
            String value = stringRedisTemplate.opsForValue().get(key);
            if(StringUtils.isBlank(value)){
                CallAccountPO accountPO = callAccountMapper.selectById(id);
                if(null == accountPO)
                    throw  new CrmException(ResultVO.ResultCode.FAIL,"该呼叫功能账号不存在");
                stringRedisTemplate.opsForValue().set(key,accountPO.toString(),2, TimeUnit.HOURS);
                return accountPO;
            }else{
                return JSONUtil.toJavaBean(value,CallAccountPO.class);
            }
        }
    }
}
