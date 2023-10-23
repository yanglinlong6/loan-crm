package com.help.loan.distribute.service.api.dao;

import com.help.loan.distribute.HelpLoanDistributeApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HelpLoanDistributeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DispatcheRecDaoTest {

    @Autowired
    private DispatcheRecDao dispatcheRecDao;

    @Test
    public void countReDispatcheNum() {
        Integer integer = dispatcheRecDao.countReDispatcheNum(20129L);
        System.out.println("integer = " + integer);
    }

    @Test
    public void test01() {
        Integer weight3 = dispatcheRecDao.countReDispatcheNum(201293L);
        log.info("orgId==" + 20129 + ";weight3==" + weight3);
        if (Objects.nonNull(weight3) && !weight3.equals(0)) {
            BigDecimal value = BigDecimal.valueOf(0.0130815105415811).divide(BigDecimal.valueOf(weight3), 3, RoundingMode.HALF_UP);
            System.out.println("value = " + value);
        }
        System.out.println("weight3 = " + weight3);
    }

}