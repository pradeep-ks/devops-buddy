package in.devopsbuddy.configuration;

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
}
