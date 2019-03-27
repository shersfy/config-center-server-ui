package com.edpglobal.config.server.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigServerFilter implements Filter{
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(ConfigServerFilter.class);

	protected static final String[] IgnoreUrls 		= {};
	protected static final String[] IgnoreResources = {".html", ".js", ".css", ".ico", ".jpg", ".png"};
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req  = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		LOGGER.debug("url={}", req.getRequestURL());
		
		setBasePath(req, res);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}
	
	protected boolean ignore(String url) {
		for (String ignore :IgnoreUrls) {
			if (ignore.contains("*")) {
				ignore = ignore.replace("*", "");
				if ((url+"/").startsWith(ignore)) {
					return true;
				}
			}
			if (url.equals(ignore)) {
				return true;
			}
		}
		for (String ignore :IgnoreResources) {
			if (url.endsWith(ignore)) {
				return true;
			}
		}
		return false;
	}

	protected void setBasePath(HttpServletRequest req, HttpServletResponse res) {

		res.setCharacterEncoding("UTF-8");
		res.setHeader("Access-Control-Allow-Credentials", "true");
		res.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		res.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
		res.setHeader("Access-Control-Allow-Headers", "*");

		StringBuilder basePath = new StringBuilder(0);
		basePath.append(req.getScheme()).append("://");
		basePath.append(req.getServerName());
		if(req.getServerPort() != 80 && req.getServerPort() != 443){
			basePath.append(":").append(req.getServerPort());
		}
		basePath.append(req.getContextPath());
		req.setAttribute("basePath", basePath.toString());
	}

}
