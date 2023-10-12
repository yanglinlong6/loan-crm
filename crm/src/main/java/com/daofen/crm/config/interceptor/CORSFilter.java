package com.daofen.crm.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(filterName = "CORSFilter",urlPatterns = "/")
@Component
public class CORSFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(CORSFilter.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        log.info("CORSFilter跨域过滤器：url地址：{}", req.getRequestURI());

        // 获取允许跨域的的域名地址,如果为空，则不允许跨域
//        Object value = redisTemplate.opsForHash().get("cors.properties", "cors.ip");
//        if(null != value) {
//            String origin = req.getHeader("Origin");
//            String ipListString = value.toString();
//            List<String> list = Arrays.asList(ipListString.split(","));
//            if(list.contains(origin)) {
//                log.info("匹配的跨域地址：{}", origin);
//                response.setHeader("Access-Control-Allow-Origin", origin);
//                response.setHeader("Allow", "*");
//                response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
//                response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, Authorization, X-Auth-Token");
//                response.setHeader("Access-Control-Max-Age", "3600");
//                // 接收跨域的cookie
//                response.setHeader("Access-Control-Allow-Credentials", "true");
//                if("IE".equals(req.getParameter("type"))) {
//                    response.setHeader("XDomainRequestAllowed", "1");
//                }
//            }
//        }
        String origin = req.getHeader("Origin");
        log.info("匹配的跨域地址：{}", origin);
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Allow", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, Authorization, X-Auth-Token");
        response.setHeader("Access-Control-Max-Age", "3600");
        // 接收跨域的cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if("IE".equals(req.getParameter("type"))) {
            response.setHeader("XDomainRequestAllowed", "1");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


}
