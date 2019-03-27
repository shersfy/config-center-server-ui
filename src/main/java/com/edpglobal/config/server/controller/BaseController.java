package com.edpglobal.config.server.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.edpglobal.config.server.config.I18nCodes;
import com.gouuse.datahub.commons.beans.Result.ResultCode;
import com.gouuse.datahub.i18n.I18nMessages;

public abstract class BaseController implements I18nCodes {

	protected static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	protected static final int SUCESS = ResultCode.SUCESS;
	protected static final int FAIL	  = ResultCode.FAIL;
	
	private static ThreadLocal<HttpServletRequest> THREAD_LOCAL_REQUEST = new ThreadLocal<>();
	private static ThreadLocal<HttpServletResponse> THREAD_LOCAL_RESPONSE = new ThreadLocal<>();
	
	@Autowired
	private I18nMessages i18n;
	
	@ModelAttribute
	public void setRequestAndResponse(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("request_start", String.valueOf(System.currentTimeMillis()));
		THREAD_LOCAL_REQUEST.set(request);
		THREAD_LOCAL_RESPONSE.set(response);
	}

	public HttpServletRequest getRequest() {
		return THREAD_LOCAL_REQUEST.get();
	}

	public HttpServletResponse getResponse() {
		return THREAD_LOCAL_RESPONSE.get();
	}
	

	protected String getI18nMsg(String i18nCode, Object ...args) {
		String lang = getRequest().getHeader("lang");
		lang = StringUtils.isBlank(lang) ?Locale.CHINA.toString() :lang;
		String imsg = i18n.getI18n(lang)!=null ?i18n.getI18n(lang).getProperty(i18nCode, args) :"";
		return imsg;
	}

	public String errors(List<ObjectError> list) {
		StringBuffer msg = new StringBuffer(0);
		String lang = getRequest().getHeader("lang");
		lang = StringUtils.isBlank(lang) ?Locale.CHINA.toString() :lang;
		if (list!=null){
			for (ObjectError err: list){

				String code		= err.getCode();
				String name		= err.getObjectName();
				Object[] args	= err.getArguments();
				Object rejected = null;
				
				List<Object> argList = new ArrayList<>();
				if (err instanceof FieldError){
					FieldError fErr = (FieldError) err;
					name = fErr.getField();
					code = fErr.getDefaultMessage();
					rejected = fErr.getRejectedValue();
				}

				argList.add(name);
				if (args!=null){
					List<Object> subargs = new ArrayList<>();
					for(Object arg :args){
						if (arg instanceof Pattern.Flag[]) {
							subargs.add(rejected);
							continue;
						}
						if (arg instanceof DefaultMessageSourceResolvable){
							continue;
						}
						if (arg instanceof Boolean
								&& (DecimalMin.class.getSimpleName().equals(code)
							       || DecimalMax.class.getSimpleName().equals(code))){
							continue;
						}
						subargs.add(arg);
					}
					if (MSGC000006.equals(err.getDefaultMessage())) {
						subargs.sort((o1, o2)->o1.toString().compareTo(o2.toString()));
					}
					argList.addAll(subargs);
				}
				
				if (rejected!=null){
					argList.add(rejected);
				}
				
				String imsg = i18n!=null && i18n.getI18n(lang)!=null ?i18n.getI18n(lang).getProperty(err.getDefaultMessage()) :"";
				imsg = StringUtils.isNotBlank(imsg) ?String.format(imsg, argList.toArray()): imsg;
				
				msg.append(imsg).append(";");
			}
		}
		return msg.toString();
	}

}
