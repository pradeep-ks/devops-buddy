package in.devopsbuddy.web.service.impl;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.devopsbuddy.persistence.domain.PasswordResetToken;
import in.devopsbuddy.persistence.repository.PasswordResetTokenRepository;
import in.devopsbuddy.persistence.repository.UserRepository;
import in.devopsbuddy.web.service.PasswordResetTokenService;

@Service
@Transactional(readOnly = true)
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetTokenServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${app.token.expiration-in-minutes}")
    private int expirationInMinutes;

    @Override
    public PasswordResetToken findByToken(String token) {
        return this.passwordResetTokenRepository.findByToken(token).orElse(null);
    }

    @Override
    @Transactional
    public PasswordResetToken generatePasswordResetTokenForEmail(String email) {
        PasswordResetToken passwordResetToken = null;
        var userFound = this.userRepository.findByEmail(email);
        if (userFound.isPresent()) {
            var user = userFound.get();
            var token = UUID.randomUUID().toString();
            var now = LocalDateTime.now(Clock.systemUTC());
            passwordResetToken = new PasswordResetToken(token, user, now, expirationInMinutes);
            passwordResetToken = passwordResetTokenRepository.saveAndFlush(passwordResetToken);
            LOGGER.debug("Successfully generated token {} for user {}", token, user.getUsername());
        } else {
            LOGGER.debug("Could not generate token as user with email {} not found", email);
        }
        return passwordResetToken;
    }
}
