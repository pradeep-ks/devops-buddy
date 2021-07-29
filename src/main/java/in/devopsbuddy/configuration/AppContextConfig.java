package in.devopsbuddy.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "in.devopsbuddy.persistence.repository")
@EntityScan(basePackages = "in.devopsbuddy.persistence.domain")
@EnableTransactionManagement
@PropertySource("file:///${user.home}/.devopsbuddy/application-common.properties")
public class AppContextConfig {

    @Value("${aws.s3.profile}")
    private String awsProfileName;

    @Bean
    public AmazonS3 amazonS3() {
        AWSCredentials credentials = new ProfileCredentialsProvider(awsProfileName).getCredentials();
        AmazonS3 client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
        Region region = Region.getRegion(Regions.US_EAST_2);
        client.setRegion(region);
        return client;
    }
}
