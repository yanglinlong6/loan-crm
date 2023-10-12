package com.help.loan.distribute.controller;


import com.help.loan.distribute.common.ResultCode;
import com.help.loan.distribute.common.ResultVO;
import com.help.loan.distribute.common.utils.MD5Util;
import com.help.loan.distribute.service.md5.MobileMd5Service;
import com.help.loan.distribute.service.md5.model.MobileMd5PO;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.io.*;

@RestController
public class MobileMd5Controller {

    private static final Logger log = LoggerFactory.getLogger(MobileMd5Controller.class);

    @Autowired
    private MobileMd5Service mobileMd5Service;


    @GetMapping("/md5/mobile")
    @ResponseBody
    public ResultVO loadMobileMd5(@PathParam("orgName")String orgName,@PathParam("city")String city,@PathParam("path")String path) throws IOException {
        try{
            if(StringUtils.isBlank(path) || StringUtils.isBlank(city))
                return new ResultVO(ResultCode.SUCCESS,"加载异常:path不能为空! 或者 city参数不能空");

            File file = new File(path);
            if(!file.exists())
                return new ResultVO(ResultCode.SUCCESS,"加载异常:path不存在!");
            new MobileMd5Thread(orgName,city,file,mobileMd5Service).start();
            return new ResultVO(ResultCode.SUCCESS,"加载成功,请稍后....");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultVO(ResultCode.SUCCESS,"加载异常:"+e.getMessage());
        }

    }


    public static class MobileMd5Thread extends Thread{

        private MobileMd5Service mobileMd5Service;

        private String orgName;

        private String city;

        private File file;



        public MobileMd5Thread(String city, File file,MobileMd5Service mobileMd5Service) {
            this.city = city;
            this.file = file;
            this.mobileMd5Service=mobileMd5Service;
        }

        public MobileMd5Thread(String orgName,String city, File file,MobileMd5Service mobileMd5Service) {
            this.orgName=orgName;
            this.city = city;
            this.file = file;
            this.mobileMd5Service=mobileMd5Service;
        }


        private static int i=0;
        @SneakyThrows
        @Override
        public void run() {
            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                reader.lines().forEach(line ->{
                    if(StringUtils.isNotBlank(line)){
                        line = line.replaceAll("\"","").trim();
                        if(line.matches("[0-9]+") && line.length() == 11){
                            line = MD5Util.getMd5String(line);
                        }
                        System.out.println(++i +"--->"+line);
                        mobileMd5Service.addMobileMd5(new MobileMd5PO(orgName,city,line.trim().toLowerCase()));
                    }
                });
                i=0;
                log.info("处理{"+city+"}-除重包完毕！");
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(null != reader){
                    reader.close();
                }
            }
        }
    }

//    public static void main(String[] args){
//
//        String mobile = "13632965527";
//
//        boolean matche = mobile.matches("[0-9]+");
//
//        System.out.println(matche+ ".."+MD5Util.getMd5String(mobile).length());
//
//    }


}
