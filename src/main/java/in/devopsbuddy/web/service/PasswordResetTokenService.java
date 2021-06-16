package in.devopsbuddy.web.service;

import in.devopsbuddy.persistence.domain.PasswordResetToken;

public interface PasswordResetTokenService {
    
    PasswordResetToken findByToken(String token);
    
    PasswordResetToken generatePasswordResetTokenForEmail(String email);
    
}
