package com.crm.controller;

import com.crm.common.CrmConstant;
import com.crm.common.ResultVO;
import com.crm.controller.login.LoginUtil;
import com.crm.service.cache.CacheConfigService;
import com.crm.service.employee.model.OrgEmployeeBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Controller
public class UploadController {

    private static final Logger log = LoggerFactory.getLogger(UploadController.class);

    private static final String C = "_";

    @Autowired
    CacheConfigService cacheConfigService;

    @PostMapping("/upload")
    @ResponseBody
    public ResultVO upload(@RequestParam("file") MultipartFile multipartFile){
        if (null == multipartFile || multipartFile.isEmpty()) {
            return ResultVO.fail("上传失败，请选择文件",null);
        }
        String fileName = multipartFile.getOriginalFilename();
        String dic = cacheConfigService.getCacheConfigValue(CrmConstant.Config.Upload.UPLOAD, CrmConstant.Config.Upload.SOURCE_DIC);
        if(StringUtils.isBlank(dic))
            return ResultVO.fail("保存合同目录未配置",null);
        if(!dic.endsWith(File.separator)){
            dic = dic+File.separator;
        }
        OrgEmployeeBO employee = LoginUtil.getLoginEmployee();
        String fileName2 = employee.getOrgId()+C+employee.getId()+C+uuid()+C+fileName;
        String[] array = fileName.split(".");
        if(array.length == 2){
            fileName2 = array[0]+C+System.currentTimeMillis()+array[1];
        }
        String destPath = dic + fileName2;
        try {
            File dest = new File(destPath);
            multipartFile.transferTo(dest);
            //上传文件完成之后,执行ssh命令,将文件上传到nginx服务目录
//            String targetStr = cacheConfigService.getCacheValue(CrmConstant.Redis.Upload.FIELDD,CrmConstant.Redis.Upload.SCP_TARGET);
//            String targetDic = cacheConfigService.getCacheValue(CrmConstant.Redis.Upload.FIELDD,CrmConstant.Redis.Upload.SCP_TARGET_DIC);
//            if(StringUtils.isBlank(targetStr) || StringUtils.isBlank(targetDic))
//                return ResultVO.success("未配置web地址或者目录!",null);
//            String[] targetArray = targetStr.split(",");
//            for(String target : targetArray){
//                String[] array = target.split("-");
//                SSHUtil.scp(destPath,targetDic,array[0],array[1],array[2]);
//            }
            String domain2 = cacheConfigService.getCacheConfigValue(CrmConstant.Config.Upload.UPLOAD, CrmConstant.Config.Upload.DOMAIN2);
            return ResultVO.success("文件上传成功",domain2+fileName2);
        }catch (Exception e){
            log.error("文件上传失败:{},{}",e.getMessage(),e);
            return ResultVO.fail("文件上传失败",null);
        }
    }


    private String uuid(){
        return UUID.randomUUID().toString().toLowerCase().replaceAll("-","");
    }

}
