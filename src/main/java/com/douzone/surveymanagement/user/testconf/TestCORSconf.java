package com.douzone.surveymanagement.user.testconf;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TestCORSconf implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000","https://survey-project-by-gyu.s3.ap-northeast-2.amazonaws.com/")
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
