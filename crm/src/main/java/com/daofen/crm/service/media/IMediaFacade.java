package com.daofen.crm.service.media;

import com.daofen.crm.base.CrmConstant;
import com.daofen.crm.base.DigestUtil;
import com.daofen.crm.base.PageVO;
import com.daofen.crm.controller.login.LoginUtil;
import com.daofen.crm.service.counselor.model.CompanyCounselorBO;
import com.daofen.crm.service.media.model.MediaPO;
import com.daofen.crm.service.media.model.SRCConfigPO;
import com.daofen.crm.service.media.model.SourcePO;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Component
public class IMediaFacade implements MediaFacade{
	
	@Autowired
	private MediaService mediaService;
	
	@Autowired
	private SourceService sourceService;
	
	@Autowired
	private SrcConfigService srcConfigService;

	@Override
	public PageVO<MediaPO> getMediaList(PageVO<MediaPO> vo) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		MediaPO param = vo.getParam();
		if(param ==null) {
			param = new MediaPO();
		}
		param.setCompanyId(loginThreadLocal.getCompanyId());
		vo.setParam(param);
		return mediaService.getCPMediaList(vo);
	}

	@Override
	public void addMedia(MediaPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setCompanyId(loginThreadLocal.getCompanyId());
		KeyPairGenerator generator =null;
		try {
			generator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		generator.initialize(1024);
		KeyPair keyPair = generator.generateKeyPair();
		String privateKey = new String(Base64.encodeBase64(keyPair.getPrivate().getEncoded()));
		String publicKey = new String(Base64.encodeBase64(keyPair.getPublic().getEncoded()));
		po.setPrivateKey(privateKey);
		po.setPublicKey(publicKey);
		Random r = new Random();
		po.setAppid(DigestUtil.digestMd5LowerCase32(CrmConstant.Media.APPID_PREFIX.PREFIX+po.getName()+"-"+po.getShorthand()+"-"+loginThreadLocal.getCompanyId()+"-"+r.nextInt(1000)));
		String replace = UUID.randomUUID().toString().replace("-", "");
		po.setSecret(replace);
		po.setCreateBy(loginThreadLocal.getName());
		po.setCreateDate(new Date());
		po.setState(CrmConstant.Media.State.OPEN);
		mediaService.addCPMedia(po);
	}
	
	@Override
	public void updateMedia(MediaPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setUpdateBy(loginThreadLocal.getName());
		po.setUpdateDate(new Date());
		mediaService.updateCPMedia(po);
	}

	@Override
	public void delMedia(Long id) {
		mediaService.delCPMedia(id);
	}

	@Override
	public PageVO<SourcePO> getSourceList(PageVO<SourcePO> vo) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		SourcePO param = vo.getParam();
		if(param ==null) {
			param = new SourcePO();
		}
		param.setCompanyId(loginThreadLocal.getCompanyId());
		vo.setParam(param);
		return sourceService.getSourceList(vo);
	}

	@Override
	public void addSource(SourcePO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setCreateBy(loginThreadLocal.getName());
		po.setCreateDate(new Date());
		po.setCompanyId(loginThreadLocal.getCompanyId());
		sourceService.addSource(po);
	}

	@Override
	public void updateSource(SourcePO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setCreateBy(loginThreadLocal.getName());
		po.setCreateDate(new Date());
		sourceService.updateSource(po);
	}

	@Override
	public void delSource(Long id) {
		sourceService.delSource(id);
	}

	@Override
	public PageVO<SRCConfigPO> getConfigList(PageVO<SRCConfigPO> vo) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		SRCConfigPO param = vo.getParam();
		if(param ==null) {
			param = new SRCConfigPO();
		}
		param.setCompanyId(loginThreadLocal.getCompanyId());
		vo.setParam(param);
		return srcConfigService.getConfigList(vo);
	}

	@Override
	public void addConfig(SRCConfigPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setCompanyId(loginThreadLocal.getCompanyId());
		po.setCreateBy(loginThreadLocal.getName());
		po.setConfigState(0);
		po.setCreateDate(new Date());
		srcConfigService.addSRCConfigPO(po);
	}

	@Override
	public void updateConfig(SRCConfigPO po) {
		CompanyCounselorBO loginThreadLocal = LoginUtil.getLoginUser();
		po.setUpdateBy(loginThreadLocal.getName());
		po.setUpdateDate(new Date());
		srcConfigService.updateSRCConfigPO(po);
	}

	@Override
	public void delConfig(Long id) {
		srcConfigService.delSRCConfigPO(id);
	}
	
}
