package com.visakhatourism.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploads directory
        Path uploadPath = Paths.get(uploadDir).toFile().exists()
                ? Paths.get(uploadDir).toAbsolutePath().normalize()
                : Paths.get("..", uploadDir).toAbsolutePath().normalize();
        String uploadResourceLocation = "file:" + uploadPath.toString().replace("\\", "/") + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadResourceLocation);

        // Serve frontend files from relative directory "./frontend/" or "../frontend/"
        Path frontendPath = Paths.get("frontend").toFile().exists()
                ? Paths.get("frontend").toAbsolutePath().normalize()
                : Paths.get("../frontend").toAbsolutePath().normalize();
        String frontendResourceLocation = "file:" + frontendPath.toString().replace("\\", "/") + "/";
        registry.addResourceHandler("/**")
                .addResourceLocations(frontendResourceLocation, "classpath:/static/", "classpath:/public/");
    }
}
