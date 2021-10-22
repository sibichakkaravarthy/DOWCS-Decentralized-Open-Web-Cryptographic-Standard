package me.twodee.dowcsttp;

import org.apache.tomcat.util.net.openssl.ciphers.Encryption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.view.MustacheViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.ViewResolver;

@SpringBootApplication
public class DowcsttpApplication {
    @Bean
    public ViewResolver getViewResolver(ResourceLoader resourceLoader) {

        return new MustacheViewResolver();
    }
    public static void main(String[] args) {
        SpringApplication.run(DowcsttpApplication.class, args);
    }
}
