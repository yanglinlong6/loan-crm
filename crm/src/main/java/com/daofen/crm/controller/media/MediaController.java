package com.daofen.crm.controller.media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.daofen.crm.base.PageVO;
import com.daofen.crm.base.ResultVO;
import com.daofen.crm.controller.AbstractController;
import com.daofen.crm.service.media.MediaFacade;
import com.daofen.crm.service.media.model.MediaPO;
import com.daofen.crm.service.media.model.SRCConfigPO;
import com.daofen.crm.service.media.model.SourcePO;

@RestController
public class MediaController extends AbstractController{
	
	@Autowired
	private MediaFacade mediaFacade;
	
	@RequestMapping("/media/list/get")
    @ResponseBody
    public ResultVO getMediaList(@RequestBody()PageVO<MediaPO> pageVO){
        return this.success(mediaFacade.getMediaList(pageVO));
    }
	
	@RequestMapping("/media/add")
    @ResponseBody
    public ResultVO addMedia(@RequestBody() MediaPO po){
		mediaFacade.addMedia(po);
        return this.success();
    }
	
	@RequestMapping("/media/update")
    @ResponseBody
    public ResultVO updateMedia(@RequestBody() MediaPO po){
		mediaFacade.updateMedia(po);
        return this.success();
    }
	
	@RequestMapping("/media/del")
    @ResponseBody
    public ResultVO delMedia(@RequestParam Long id){
		mediaFacade.delMedia(id);
        return this.success();
    }
	
	@RequestMapping("/media/list/del")
    @ResponseBody
    public ResultVO delMediaList(@RequestParam String idList){
		String[] split = idList.split(",");
		for(String s:split) {
			mediaFacade.delMedia(Long.valueOf(s));
		}
        return this.success();
    }
	
	@RequestMapping("/media/source/list/get")
    @ResponseBody
    public ResultVO getSourceList(@RequestBody()PageVO<SourcePO> pageVO){
        return this.success(mediaFacade.getSourceList(pageVO));
    }
	
	@RequestMapping("/media/source/add")
    @ResponseBody
    public ResultVO addSource(@RequestBody() SourcePO po){
		mediaFacade.addSource(po);
        return this.success();
    }
	
	@RequestMapping("/media/source/update")
    @ResponseBody
    public ResultVO updateSource(@RequestBody() SourcePO po){
		mediaFacade.updateSource(po);
        return this.success();
    }
	
	@RequestMapping("/media/source/del")
    @ResponseBody
    public ResultVO delSource(@RequestParam Long id){
		mediaFacade.delSource(id);
        return this.success();
    }
	
	@RequestMapping("/media/source/list/del")
    @ResponseBody
    public ResultVO delSourceList(@RequestParam String idList){
		String[] split = idList.split(",");
		for(String s:split) {
			mediaFacade.delSource(Long.valueOf(s));
		}
        return this.success();
    }
	
	@RequestMapping("/media/source/config/list/get")
    @ResponseBody
    public ResultVO getConfigList(@RequestBody()PageVO<SRCConfigPO> pageVO){
        return this.success(mediaFacade.getConfigList(pageVO));
    }
	
	@RequestMapping("/media/source/config/add")
    @ResponseBody
    public ResultVO addConfig(@RequestBody() SRCConfigPO po){
		mediaFacade.addConfig(po);
        return this.success();
    }
	
	@RequestMapping("/media/source/config/update")
    @ResponseBody
    public ResultVO updateConfig(@RequestBody() SRCConfigPO po){
		mediaFacade.updateConfig(po);
        return this.success();
    }
	
	@RequestMapping("/media/source/config/list/del")
    @ResponseBody
    public ResultVO delConfig(@RequestParam String idList){
		String[] split = idList.split(",");
		for(String s:split) {
			mediaFacade.delConfig(Long.valueOf(s));
		}
        return this.success();
    }
	
	@RequestMapping("/media/source/config/del")
    @ResponseBody
    public ResultVO delConfigList(@RequestParam Long id){
		mediaFacade.delConfig(id);
        return this.success();
    }
	
}
