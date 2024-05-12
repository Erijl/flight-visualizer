package com.erijl.flightvisualizer.backend.config;

import com.erijl.flightvisualizer.backend.config.converter.ProtobufJsonFormatHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new ProtobufJsonFormatHttpMessageConverter());
    }
}