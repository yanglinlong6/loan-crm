package com.daofen.admin.controller.media;


import com.daofen.admin.basic.ResultCode;
import com.daofen.admin.service.media.AdvertisingServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.daofen.admin.basic.AbstractController;
import com.daofen.admin.basic.ResultVO;
import com.daofen.admin.service.media.AdvertisingService;

@RestController
public class AdvertisingController extends AbstractController {
	private static final Logger log = LoggerFactory.getLogger(AdvertisingController.class);
	@Autowired
	private AdvertisingService advertisingService;
	
	@RequestMapping("/advertising/report/upload")
    @ResponseBody
    public ResultVO upload(@RequestParam("file") MultipartFile file){
		String name = file.getOriginalFilename();
		System.out.println(name);
		if(!name.endsWith(".xlsx")) {
			return this.failed();
		}
		try {
			advertisingService.addAdvertising(file);
		} catch (Exception e) {
			log.error("/advertising/report/upload处理异常:{}",e.getMessage(),e);
			return this.failed(ResultCode.FAID, e.getMessage());
		}
		
        return this.success();
    }
	
}
