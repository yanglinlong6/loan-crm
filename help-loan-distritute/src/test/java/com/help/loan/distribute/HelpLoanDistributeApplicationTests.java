package com.help.loan.distribute;

import com.help.loan.distribute.common.utils.excel.XLSXCovertCSVReader;
import com.help.loan.distribute.schedule.CpaScheduleApi;
import com.help.loan.distribute.service.api.*;
import com.help.loan.distribute.service.api.utils.JudgeUtil;
import com.help.loan.distribute.service.user.dao.UserAptitudeDao;
import com.help.loan.distribute.service.user.model.UserAptitudePO;
import com.help.loan.distribute.util.DESUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.List;

@SpringBootTest(classes = HelpLoanDistributeApplication.class)
class HelpLoanDistributeApplicationTests {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserAptitudeDao dao;

//    @Qualifier("")
//    @Autowired
//    private CpaSchedule cpaSchedule;


//    @Test
//    void contextLoads() throws InterruptedException {
////        cpaAPIScheduledService.send();
//
//        stringRedisTemplate.opsForValue().set("aa.test.key.1", "test.key.one", 10, TimeUnit.SECONDS);
//
//        Thread.sleep(5000);
//
//        stringRedisTemplate.opsForValue().set("aa.test.key.1", "test.key.tow");
//    }

//    @Autowired
//    QianzhiApi qianzhiApi;

    @Autowired
    PingAnApi pingAnApi;

    @Autowired
    Rong360Api rong360Api;

    @Autowired
    YuLe2Api yuLe2Api;

    @Test
    public  void test() throws IOException {

        List<UserAptitudePO> byLevel = dao.getByLevelByLimitDate("1,2,3,4,5,6,7,8,9,99","2022-10-01 00:00:00","2022-10-30 23:59:59");
        RandomAccessFile randomFile = new RandomAccessFile("D:\\排重包\\all.txt", "rw");
        for(UserAptitudePO userAptitudePO : byLevel){
            if(userAptitudePO.getChannel().endsWith("=") || userAptitudePO.getName().contains("*")){
                continue;
            }
            System.out.println(userAptitudePO.getName());
            randomFile.seek(randomFile.length());
            randomFile.write((userAptitudePO.toString()+"\r\n").getBytes("utf-8"));
            UserAptitudePO newPo = new UserAptitudePO();
            newPo.setName(CpaScheduleApi.encryptName(userAptitudePO.getName()));
            newPo.setMobile(CpaScheduleApi.encryptMobile(userAptitudePO.getMobile()));
            newPo.setUpdateDate(userAptitudePO.getCreateDate());
            newPo.setUserId(userAptitudePO.getUserId());
            if(JudgeUtil.in(userAptitudePO.getLevel(),9))
                newPo.setLevel(99);
            dao.update(newPo);
        }
        randomFile.close();
//        try {
//            testExcel(null);
//        }catch (Exception e){
//            e.printStackTrace();
//        }

    }

//    public void testExcel(String path) throws IOException, OpenXML4JException, ParserConfigurationException, SAXException {
//        FileInputStream inputStream = new FileInputStream(new File(path));
//        List<String[]> datas = XLSXCovertCSVReader.readerExcel(inputStream, "Sheet1", 2);
//        if(null == datas || datas.isEmpty()){
//            return;
//        }
//        for(int i=0;i<datas.size();i++){
//            String[] array = datas.get(i);
//            String name = array[0];
//            String mobile = array[1];
//            System.out.println(name+"----"+mobile);
//            dao.
//        }
//    }


//    @Transactional
    public void updateUserAptitude(Integer level, String userId) {
        UserAptitudePO userAptitudePO = new UserAptitudePO();
        userAptitudePO.setLevel(level);
        userAptitudePO.setUserId(userId);
        dao.update(userAptitudePO);
    }




}
