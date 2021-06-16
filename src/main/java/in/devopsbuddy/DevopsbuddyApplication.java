package in.devopsbuddy;

import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import in.devopsbuddy.enums.PlanEnum;
import in.devopsbuddy.enums.RoleEnum;
import in.devopsbuddy.persistence.domain.Role;
import in.devopsbuddy.persistence.domain.UserRole;
import in.devopsbuddy.util.UserUtil;
import in.devopsbuddy.web.service.UserService;

@SpringBootApplication
public class DevopsbuddyApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevopsbuddyApplication.class);

	@Autowired
	private UserService userService;

	@Value("${webmaster.username}")
	private String username;

	@Value("${webmaster.password}")
	private String password;

	@Value("${webmaster.email}")
	private String email;

	public static void main(String[] args) {
		SpringApplication.run(DevopsbuddyApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		/* var username = "proUser";
		var email = "user.pro@devopsbuddy.in"; */

		var user = UserUtil.createBasicUser(username, email);
		user.setPassword(password);
		var userRoles = new HashSet<UserRole>();
		userRoles.add(new UserRole(user, new Role(RoleEnum.ADMIN)));

		LOGGER.debug("Creating new user with username {}", user.getUsername());
		
		user = userService.save(user, PlanEnum.PRO, userRoles);
		
		LOGGER.info("User {} created", user.getUsername());
	}

}
