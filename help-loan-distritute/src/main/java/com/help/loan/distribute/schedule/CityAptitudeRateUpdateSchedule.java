package com.help.loan.distribute.schedule;

import com.help.loan.distribute.common.utils.DateUtil;
import com.help.loan.distribute.service.cityAptitude.CityAptitudeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CityAptitudeRateUpdateSchedule implements CpaSchedule {
    private static final Logger log = LoggerFactory.getLogger(CityAptitudeRateUpdateSchedule.class);
    @Autowired
    private CityAptitudeService cityAptitudeService;

//    @Scheduled(cron = "0 0/5 9-23 * * ?")
    @Override
    public void send() {
        try {
            String date = DateUtil.formatToString(new Date(), "yyyy-MM-dd");
            cityAptitudeService.updateCityAptitudeRate(date);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }

    }
}
