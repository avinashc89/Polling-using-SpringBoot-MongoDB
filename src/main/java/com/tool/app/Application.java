package com.tool.app;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.tool.app.security.SecurityConfig;

@ComponentScan
@EnableAutoConfiguration
public class Application extends WebMvcConfigurerAdapter{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
   
}