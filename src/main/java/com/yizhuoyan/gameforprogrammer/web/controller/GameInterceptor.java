package com.yizhuoyan.gameforprogrammer.web.controller;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.yizhuoyan.gameforprogrammer.domain.PlayerGameStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by ben on 10/27/18.
 */
public class GameInterceptor implements HandlerInterceptor {

	static Integer parseInt(String s) {
		int numberEndIndex = 0;
		char c = 0;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if ('0' <= c && c <= '9') {
				numberEndIndex++;
			} else {
				break;
			}
		}
		s = s.substring(0, numberEndIndex);
		if(s.length()==0)return null;
		return Integer.parseInt(s, 10);
	}

	private Integer getRequestLevel(HttpServletRequest req){
		String uri = req.getRequestURI();
		uri = uri.substring(req.getContextPath().length() + 1);
		try {
			Integer requestLevel = parseInt(uri);
			return requestLevel;
		}catch (NumberFormatException e){
			return null;
		}
	}
	@Override

	public boolean preHandle(HttpServletRequest req, HttpServletResponse response, Object handler) throws Exception {
		//第一次进入
		if(req.getRequestedSessionId()==null){
			response.sendRedirect("/");
			return false;
		}

		HttpSession session = req.getSession(false);
		//session超时
		if(session==null){
			req.getRequestDispatcher("/timeout").forward(req, response);
			return false;
		}
		//获取请求关卡
		Integer requestLevel=getRequestLevel(req);
		if(requestLevel==null) {
			//继续当前关卡
			req.getRequestDispatcher("/continue").forward(req, response);
			return false;
		}
		PlayerGameStatus pgs = (PlayerGameStatus) session.getAttribute(PlayerGameStatus.class.getName());
		// 无当前关卡信息
		if (pgs == null) {
			// timeout,从第一关进行
			req.getRequestDispatcher("/timeout").forward(req, response);
			return false;
		}
		// 请求关卡和实际关卡一样，通过
		if (requestLevel == pgs.getCurrentLevel()) {
			return true;
			// 请求关卡小于实际关卡，询问是否重来?
		} else if (requestLevel < pgs.getCurrentLevel()) {
			req.getRequestDispatcher("/need-restart").forward(req, response);
			return false;
		} else {
			// 请求关卡大于实际关卡，警告并返回当前关卡
			req.getRequestDispatcher("/need-warning").forward(req, response);
			return false;
		}
	}


}
