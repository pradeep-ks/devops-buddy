package in.devopsbuddy;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import in.devopsbuddy.web.i18n.I18NService;

@SpringBootTest
class DevopsbuddyApplicationTests {

	@Autowired
	private I18NService i18nService;

	@Test
	public void testMessageByLocaleService() throws Exception {
		String expected = "Welcome to DevOps Buddy";
		String messageId = "index.main.callout";
		String actual = this.i18nService.getMessage(messageId);
		assertEquals(expected, actual, "The actual and expected strings don't match");
	}
}
