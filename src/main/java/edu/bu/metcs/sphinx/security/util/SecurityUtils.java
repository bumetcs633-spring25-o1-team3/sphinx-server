package edu.bu.metcs.sphinx.security.util;

import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtils {

    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof SphinxOAuth2User user) {
            return user.getUserId();
        }
        throw new RuntimeException("User not authenticated");
    }

    public static void verifyCurrentUser(UUID userId) {
        UUID currentUserId = getCurrentUserId();
        if (!currentUserId.equals(userId)) {
            throw new RuntimeException("Unauthorized access");
        }
    }
}