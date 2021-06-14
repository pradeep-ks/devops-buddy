package in.devopsbuddy.web.i18n;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class I18NService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(I18NService.class);

    @Autowired
    private MessageSource messageSource;

    public String getMessage(String messageId) {
        LOGGER.info("Returning i18n text for messageId {}", messageId);
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(messageId, locale);
    }

    public String getMessage(String messageId, Locale locale) {
        return this.messageSource.getMessage(messageId, null, locale);
    }
}
