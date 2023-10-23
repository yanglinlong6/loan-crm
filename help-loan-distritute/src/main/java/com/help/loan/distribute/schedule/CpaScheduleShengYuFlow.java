package com.help.loan.distribute.schedule;

import com.help.loan.distribute.common.utils.CollectionUtil;
import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 昨天剩余流量定时分发
 */
@Component
public class CpaScheduleShengYuFlow implements CpaSchedule {
    @Autowired
    private UserAptitudeDao userAptitudeDao;

    private static final Logger log = LoggerFactory.getLogger(CpaScheduleShengYuFlow.class);

    /**
     * 将用户状态从9修改为4
     */
    @Scheduled(cron = "0 0/3 8-22 * * ?")
    @Override
    public void send() {
        log.info("0 0/3 8-22 * * ?  余量修改状态定时任务");
        // 优化将余量状态修改时间设置为 3天前 主要考虑周末时间问题
        String startDate = LocalDateTime.now().plusDays(-3).format(DateTimeFormatter.ofPattern(DateUtil.yyyyMMdd)) + " 00:00:00";
        String endDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateUtil.yyyyMMdd)) + " 23:59:59";
        List<UserAptitudePO> byLevel = userAptitudeDao.getByLevelByLimitDate("9", startDate, endDate);
        if (CollectionUtil.isEmpty(byLevel))
            return;
        Map<String, List<UserAptitudePO>> map = byLevel.stream().collect(Collectors.groupingBy(level -> level.getCity()));
        if (CollectionUtil.isEmpty(map)) {
            return;
        }
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            List<UserAptitudePO> list = map.get(key);
            if (CollectionUtil.isEmpty(list))
                continue;
            UserAptitudePO userAptitude = list.get(0);
            UserAptitudePO newUserAptitude = new UserAptitudePO();
            newUserAptitude.setLevel(4);
            newUserAptitude.setUserId(userAptitude.getUserId());
            userAptitudeDao.update(newUserAptitude);
            log.info("修改用户状态：{}-{}-{}", userAptitude.getName(), userAptitude.getMobile(), 4);
        }
    }

}
