package com.loan.wechat.entrances.controller;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.loan.wechat.entrances.service.EntrancesFacade;
import com.loan.wechat.entrances.service.IEntrancesFacade;


/**
 * 微信推送消息控制器
 * @author kongzhimin
 *
 */
@Controller
public class EventController {
	
	@Autowired
	private EntrancesFacade entrancesFacade;
	
	private static Log logger = LogFactory.getLog(EventController.class);
	
	/**
	 * 接收微信推送消息
	 * @param req
	 * @return
	 */
	@RequestMapping(value="/xmlMsgReceive")
	public void xmlMsgReceive(HttpServletRequest req,HttpServletResponse res)
	{
		if("GET".endsWith(req.getMethod())) {
			doResponse(res, req.getParameter("echostr"));
			return;
		}
		entrancesFacade.xmlMsgReceive(processRequestXml(req));
		doResponse(res, "");
	}
	
	private void doResponse(HttpServletResponse res, String responseBody) {
		PrintWriter writer = null;
		try {
			writer = res.getWriter();
			writer.println(responseBody);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * XML消息解析
	 * @param request
	 * @return
	 */
	private Map<String, String> processRequestXml(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map.put("domain2", request.getServerName().startsWith("localhost")?"localhost:8080":request.getServerName());
        map.put("status", "0");
        SAXReader reader = new SAXReader();
        ServletInputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
            Document document = reader.read(inputStream);
            Element root = document.getRootElement();
            List<Element> elements = root.elements();
            for (Element e : elements) {
                map.put(e.getName(), e.getText());
            }
        } catch (DocumentException | IOException e) {
            logger.error("微信消息解析异常",e);
        } finally {
            if(inputStream!=null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("解析微信消息IO异常",e);
                }
            }
        }
        return map;
    }

}
