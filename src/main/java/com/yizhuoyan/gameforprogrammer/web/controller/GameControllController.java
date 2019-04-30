/**
 * GameControllController.java
 */
package com.yizhuoyan.gameforprogrammer.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.yizhuoyan.gameforprogrammer.domain.PlayerGameStatus;

/**
 * @author yijun@neusoft.com
 * 
 */
@Controller
public class GameControllController {

	@GetMapping("/need-warning")
	public String dd() {
		return "/warning.html";
	}
	
	@GetMapping("/")
	public String welcome(HttpServletRequest req) {
		HttpSession session=req.getSession(false);
		if(session!=null) {//说明之前玩过
			//跳到当前关卡
			return "forward:/continue";
		}else {
			req.getSession().setAttribute(PlayerGameStatus.class.getName(), new PlayerGameStatus());
		}
		//第一次游戏
		return "/welcome.html";
	}


	@GetMapping("/timeout")
	public String timeout(HttpServletRequest req) {
		
		return "/timeout.html";
	}
	
	
	@GetMapping("/restart")
	public String restart(HttpServletRequest req) {
		HttpSession session=req.getSession(false);
		if(session!=null) {
			//请求重玩
			session.invalidate();
		}
		req.getSession().setAttribute(PlayerGameStatus.class.getName(), new PlayerGameStatus());
		return "/welcome.html";
	}
	
	@GetMapping("/need-restart")
	public String needRestart() {
		return "/restart.html";
	}
	@GetMapping("/win")
	public String win(HttpServletRequest req) {
		PlayerGameStatus pgs =(PlayerGameStatus) req.getSession().getAttribute(PlayerGameStatus.class.getName());
		if(pgs==null) {
			return "forward:/timeout";
		}
		if(pgs.getCurrentLevel()>=PlayerGameStatus.MAX_LEVEL) {
			req.getSession().invalidate();
			req.setAttribute("code", String.valueOf(Math.random()).subSequence(2, 5));
			return "/win.html";
		}
		return "forward:/need-warning";
		
	}
	@GetMapping("/continue")
	public String levelContinue(HttpServletRequest req) {
		PlayerGameStatus pgs =(PlayerGameStatus) req.getSession().getAttribute(PlayerGameStatus.class.getName());
		if(pgs==null) {
			return "redirect:/01";
		}
		return "redirect:/" + pgs.getCurrentLevelViewName();
	}
}
