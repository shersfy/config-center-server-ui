package com.edpglobal.config.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PageController extends BaseController{
	
	@RequestMapping("/")
	public ModelAndView index1() {
		return new ModelAndView("index");
	}
	
	@RequestMapping("/index")
	public ModelAndView index2() {
		return new ModelAndView("redirect:/");
	}
	
}