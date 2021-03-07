package com.yizhuoyan.gameforprogrammer.web.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.yizhuoyan.gameforprogrammer.domain.PlayerGameStatus;
import com.yizhuoyan.gameforprogrammer.util.AlgorithmUtil;

import net.glxn.qrgen.core.AbstractQRCode;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

/**
 * Created by ben on 10/25/18.
 */
@Controller
public class GameLevelController implements AlgorithmUtil {
	@Value("${spring.application.name}")
	String appName;
	
	public PlayerGameStatus getPlayerGameStatus() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = sra.getRequest().getSession();
		PlayerGameStatus pgs = (PlayerGameStatus) session.getAttribute(PlayerGameStatus.class.getName());
		// 没有则新建一个
		if (pgs == null) {
			pgs = new PlayerGameStatus();
			session.setAttribute(PlayerGameStatus.class.getName(), pgs);
		}
		return pgs;
	}
	
	public String newCurrentLevelKey() {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = sra.getRequest().getSession();
		PlayerGameStatus pgs = (PlayerGameStatus) session.getAttribute(PlayerGameStatus.class.getName());
		session.setAttribute("key", pgs.newCurrentLevelKey());
		return pgs.getCurrentLevelKey();
	}

	// 文字和背景同色
	@GetMapping("/01")
	public String level01(String key) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/02";
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/01.html";
	}

	// 注释
	@GetMapping("/02")
	public String levelInComment(String key) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/03";
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		
		return "/02.html";
	}

	// 必须get
	@RequestMapping("/03")
	public String levelMustGetMethod(String key, HttpServletRequest req) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (req.getMethod().equalsIgnoreCase("GET")) {
			if (pgs.passCurrentLevel(key)) {
				return "redirect:/04";
			}
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/03.html";
	}
	// 必须post
	@RequestMapping("/04")
	public String levelMustPostMethod(String key, HttpServletRequest req) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (req.getMethod().equalsIgnoreCase("POST")) {
			if (pgs.passCurrentLevel(key)) {
				return "redirect:/05";
			}
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/04.html";
	}

	// head
	@GetMapping("/05")
	public String levelKeyInResponceHeader(String key, HttpServletResponse resp) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/06";
		}
		// 失败，重新生成key
		resp.setHeader("key", newCurrentLevelKey());
		return "/05.html";
	}

	// cookie
	@GetMapping("/06")
	public String levelCookie(String key, HttpServletRequest req, HttpServletResponse resp) {

		PlayerGameStatus pgs = getPlayerGameStatus();
		// key-cookie消失
		if (!containsKeyCookie(req)) {
			if (pgs.passCurrentLevel(key)) {
				return "redirect:/07";
			}
		}
		// 失败，重新生成key
		Cookie cookie = new Cookie("key", newCurrentLevelKey());
		// 保证浏览器不失效
		cookie.setMaxAge(Integer.MAX_VALUE);
		cookie.setPath("/deleteMePass");
		cookie.setVersion(1);
		resp.addCookie(cookie);

		return "/06.html";
	}

	private boolean containsKeyCookie(HttpServletRequest req) {

		Cookie[] cookies = req.getCookies();
		if (cookies == null)
			return false;
		for (Cookie c : cookies) {
			if ("key".equals(c.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 二维码扫码
	 */
	@GetMapping("/07")
	public String levelScan(String key) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/08";
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/07.html";
	}

	@GetMapping("/07.png")
	public void generateBase64QRCodeImg(HttpServletResponse resp) throws IOException {
		PlayerGameStatus pgs = getPlayerGameStatus();
		resp.setContentType("image/png");
		// 失败，重新生成key
		String key = pgs.getCurrentLevelKey();
		key = base64(key);
		AbstractQRCode qrCode = QRCode.from(key);
		// 设置字符集，支持中文
		qrCode.withCharset("utf-8");
		// 设置生成的二维码图片大小
		qrCode.withSize(260, 260);
		ByteArrayOutputStream data = qrCode.to(ImageType.PNG).stream();

		try (ServletOutputStream out = resp.getOutputStream();) {
			out.write(data.toByteArray());
		}
	}

	/**
	 * 图片16进制隐藏
	 * 
	 * @param resp
	 * 
	 * @throws IOException
	 */
	@GetMapping("/08")
	public String levelHideInImg(String key, HttpServletResponse resp) throws IOException {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/09";
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/08.html";

	}

	@GetMapping("/08.png")
	public void generateHideBytesInQRImg(HttpServletResponse resp) throws IOException {
		PlayerGameStatus pgs = getPlayerGameStatus();
		resp.setContentType("image/png");
		AbstractQRCode qrCode = QRCode.from("怎么可能还这么简单？");
		// 设置字符集，支持中文
		qrCode.withCharset("utf-8");
		// 设置生成的二维码图片大小
		qrCode.withSize(260, 260);
		ByteArrayOutputStream data = qrCode.to(ImageType.PNG).stream();

		try (ServletOutputStream out = resp.getOutputStream();) {
			out.write(data.toByteArray());
			String key = pgs.getCurrentLevelKey();
			out.write(new byte[8]);//给8个空字节分割
			out.write(key.getBytes("iso-8859-1"));
		}
	}

	/**
	 * 种子图片方式
	 * 
	 * @param key
	 */
	@GetMapping("/09")
	public String levelSeedImg(String key) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/10";
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/09.html";
	}

	@GetMapping("/09.png")
	public void genereateSeedImg(HttpServletResponse resp) throws IOException {
		PlayerGameStatus pgs = getPlayerGameStatus();
		resp.setContentType("image/jpeg");
		String key = pgs.getCurrentLevelKey();
		try (ServletOutputStream out = resp.getOutputStream();) {
			AbstractQRCode qrCode = QRCode.from("你居然还这么试？");
			// 设置字符集，支持中文
			qrCode.withCharset("utf-8");
			// 设置生成的二维码图片大小
			qrCode.withSize(260, 260);
			ByteArrayOutputStream data = qrCode.to(ImageType.PNG).stream();
			out.write(data.toByteArray());
			// yasuo
			byte[] zipData = createZipFile(key);
			out.write(zipData);
		}
	}
	/**
	 * 创建压缩文件
	 * @param key
	 * @return
	 * @throws IOException
	 */
	private byte[] createZipFile(String key) throws IOException {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		try (ZipOutputStream out = new ZipOutputStream(data);) {
			out.putNextEntry(new ZipEntry("1"));
			out.write(key.substring(0, key.length() / 2).getBytes());
			out.putNextEntry(new ZipEntry("2"));
			out.write(key.substring(key.length() / 2).getBytes());
			out.finish();
			return data.toByteArray();
		}
	}

	// 2进制形式
	@GetMapping("/10")
	public String levelShowBinary(String key, HttpServletRequest req, HttpServletResponse resp) throws Exception {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/11";
		}
		// 失败，重新生成key
		key = newCurrentLevelKey();
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < key.length(); i++) {
			if(i<=8) {
				result.append(to8bitsString(key.charAt(i)));
			}else {
				result.append(key.charAt(i));
			}
		}
		req.setAttribute("tips", result.toString());
		return "/10.html";
	}

	// morse code
	@GetMapping("/11")
	public String levelShowMoreCode(String key, HttpServletRequest req) {
		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/12";
		}
		// 失败，重新生成key
		key=newCurrentLevelKey();
		String moreCode = morseCode(key.substring(0,16));
		
		req.setAttribute("tips", moreCode+" "+key.substring(16));
		return "/11.html";
	}

	// 404方式
	@GetMapping("/12")
	public String level404(String key, HttpServletRequest req) {

		PlayerGameStatus pgs = getPlayerGameStatus();
		if (pgs.passCurrentLevel(key)) {
			return "redirect:/win";
		}
		// 失败，重新生成key
		newCurrentLevelKey();
		return "/12.html";
	}

	@ExceptionHandler
	public void hanldeException(Exception e) {
		e.printStackTrace();
	}

}
