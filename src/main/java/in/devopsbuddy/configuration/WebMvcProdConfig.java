package in.devopsbuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import in.devopsbuddy.web.service.EmailService;
import in.devopsbuddy.web.service.impl.SMTPEmailService;

@Configuration
@Profile("prod")
@PropertySource("file:///${user.home}/.devopsbuddy/application-prod.properties")
public class WebMvcProdConfig {

    @Bean
    public EmailService emailService() {
        return new SMTPEmailService();
    }
}
