package com.loan.cps.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CORSFilter",urlPatterns = "/")
@Component
public class CORSFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        LOG.info("CORSFilter跨域过滤器：uri地址：{}", req.getRequestURI());
        response.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
        response.setHeader("Allow", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE,PUT");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, Authorization, X-Auth-Token");
        response.setHeader("Access-Control-Max-Age", "3600");
        // 接收跨域的cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        if ("IE".equals(req.getParameter("type"))) {
            response.setHeader("XDomainRequestAllowed", "1");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }


}
