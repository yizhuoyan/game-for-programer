package com.yizhuoyan.gameforprogrammer;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.ServletContext;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yizhuoyan.gameforprogrammer.domain.PlayerGameStatus;
import com.yizhuoyan.gameforprogrammer.web.controller.GameInterceptor;

@SpringBootApplication
public class GameForProgrammerApplication
		implements WebMvcConfigurer, ApplicationListener<ServletWebServerInitializedEvent> {

	public void addInterceptors1(InterceptorRegistry registry) {
		registry.addInterceptor(new GameInterceptor()).addPathPatterns(IntStream.range(1, PlayerGameStatus.MAX_LEVEL)
				.mapToObj(i -> i > 9 ? "/" + i : "/0" + i).collect(Collectors.toList()));
	}

	public void onApplicationEvent(ServletWebServerInitializedEvent event) {
		ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
		ServletContext servletContext = event.getApplicationContext().getServletContext();
		Arrays.asList("app.name").forEach(k -> {
			servletContext.setAttribute(k, environment.getProperty(k));
		});
	}

	public static void main(String[] args) {

		SpringApplication.run(GameForProgrammerApplication.class, args);

	}
}
