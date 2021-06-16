package in.devopsbuddy.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import in.devopsbuddy.DevopsbuddyApplication;
import in.devopsbuddy.persistence.domain.PasswordResetToken;
import in.devopsbuddy.persistence.domain.User;
import in.devopsbuddy.persistence.repository.PasswordResetTokenRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordResetIntegrationTest extends AbstractIntegrationTest {
    
    @Value("${app.token.expiration-in-minutes}")
    private int expirationInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @BeforeEach
    public void init() {
        assertFalse(expirationInMinutes == 0);
    }

    @Test
    public void testTokenExpiration(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        assertNotNull(user);
        assertNotNull(user.getId());

        var now = LocalDateTime.now(Clock.systemUTC());
        var token = UUID.randomUUID().toString();
        var expectedTime = now.plusMinutes(expirationInMinutes);

        var resetToken = createPasswordResetToken(token, user, now);
        var actualTime = resetToken.getExpiryDate();
        assertNotNull(actualTime);
        assertEquals(expectedTime, actualTime);
    }

    @Test
    public void testFindTokenByTokenValue(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        var token = UUID.randomUUID().toString();
        var now = LocalDateTime.now(Clock.systemUTC());
        createPasswordResetToken(token, user, now);
        
        var retrievedToken = passwordResetTokenRepository.findByToken(token).get();
        assertNotNull(retrievedToken);
        assertNotNull(retrievedToken.getId());
        assertNotNull(retrievedToken.getUser());
    }

    @Test
    public void testDeleteToken(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        var token = UUID.randomUUID().toString();
        var now = LocalDateTime.now(Clock.systemUTC());
        var resetToken = createPasswordResetToken(token, user, now);
        var tokenId = resetToken.getId();
        passwordResetTokenRepository.deleteById(tokenId);
        var deletedToken = passwordResetTokenRepository.findById(tokenId);
        assertTrue(deletedToken.isEmpty());
    }

    @Test
    public void testCascadeDeleteFromUser(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        var token = UUID.randomUUID().toString();
        var now = LocalDateTime.now(Clock.systemUTC());
        var resetToken = createPasswordResetToken(token, user, now);
        resetToken.getId();
        userRepository.deleteById(user.getId());
        var tokenSet = passwordResetTokenRepository.findAllByUserId(user.getId());
        assertTrue(tokenSet.isEmpty());
    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueryingByUserId(TestInfo testInfo) throws Exception {
        var user = createUser(testInfo);
        var now = LocalDateTime.now(Clock.systemUTC());
        
        var token1 = UUID.randomUUID().toString();
        var token2 = UUID.randomUUID().toString();
        var token3 = UUID.randomUUID().toString();
        
        var tokens = new HashSet<PasswordResetToken>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));
        
        passwordResetTokenRepository.saveAll(tokens);

        var userFound = userRepository.findById(user.getId()).get();
        var actualTokens = passwordResetTokenRepository.findAllByUserId(userFound.getId());
        assertTrue(actualTokens.size() == tokens.size());

        var tokensAsList = tokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        var actualTokensAsList = actualTokens.stream().map(prt -> prt.getToken()).collect(Collectors.toList());
        assertEquals(tokensAsList, actualTokensAsList);
    }

    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime now) {
        var resetToken = new PasswordResetToken(token, user, now, expirationInMinutes);
        resetToken = passwordResetTokenRepository.saveAndFlush(resetToken);
        assertNotNull(resetToken.getId());
        return resetToken;
    }
}
