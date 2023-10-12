package com.help.loan.distribute.schedule;

import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.service.org.OrgService;
import com.help.loan.distribute.service.session.SessionService;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.dao.WechatUserBindDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreditOverdueSchedule implements CpaSchedule {

    private static Logger LOG = LoggerFactory.getLogger(CreditOverdueSchedule.class);

    @Autowired
    private UserAptitudeDao dao;

    @Autowired
    private WechatUserBindDao wechatUserBindDao;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private OrgService orgService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private String ORG_DISTRIBUTE_CREDIT_TASK_10_KEY = "org_distribute_credit_task_10_key";

    @Override
    public void send() {
        try {
            if(!stringRedisTemplate.opsForValue().setIfAbsent(ORG_DISTRIBUTE_CREDIT_TASK_10_KEY, "1", 100, TimeUnit.SECONDS)) {
                LOG.error("30秒钟分发定时任务正在实行，直接返回。。。。。");
                return;
            }
            LOG.info("[信用卡逾期]定时分发任务[start]");
            List<UserAptitudePO> userList =   dao.getByLevel(10);
            if(CollectionUtil.isEmpty(userList)){
                LOG.info("[信用卡逾期]定时分发任务[no user][end]");
                return;
            }
            userList.stream().forEach(user ->{
                LOG.info("[信用卡逾期]客户：{}",user.toString());
            });
        } catch(Exception e) {
            stringRedisTemplate.opsForValue().getOperations().delete(ORG_DISTRIBUTE_CREDIT_TASK_10_KEY);
            LOG.error(e.getMessage(), e);
        }
    }

    private void send(UserAptitudePO user){
        if(null == user){
            return;
        }

    }
}
