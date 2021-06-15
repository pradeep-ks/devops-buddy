package in.devopsbuddy.configuration;

import org.h2.server.web.WebServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import in.devopsbuddy.web.service.EmailService;
import in.devopsbuddy.web.service.impl.MockEmailService;

@Configuration
@Profile("dev")
@PropertySource("file:///${user.home}/.devopsbuddy/application-dev.properties")
public class WebMvcDevConfig {
    
    @Bean
    public EmailService emailService() {
        return new MockEmailService();
    }

    @Bean
    public ServletRegistrationBean h2ConsoleServletRegistration() {
        ServletRegistrationBean<WebServlet> bean = new ServletRegistrationBean<>(new WebServlet());
        bean.addUrlMappings("/console/*");
        return bean;
    }
}
