package in.devopsbuddy.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "in.devopsbuddy.persistence.repository")
@EntityScan(basePackages = "in.devopsbuddy.persistence.domain")
@EnableTransactionManagement
public class AppContextConfig {
    
}
