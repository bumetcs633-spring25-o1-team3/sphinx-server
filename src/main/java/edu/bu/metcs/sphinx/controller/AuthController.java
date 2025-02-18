package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser(
            @AuthenticationPrincipal SphinxOAuth2User oauth2User,
            HttpServletRequest request) {

        logger.info("Auth request received. Session ID: {}", request.getSession().getId());

        // Log session attributes
        HttpSession session = request.getSession(false);
        if (session != null) {
            Enumeration<String> attributeNames = session.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                logger.info("Session attribute - {}: {}", name, session.getAttribute(name));
            }
        }

        if (oauth2User == null) {
            logger.warn("No authenticated user found");
            return ResponseEntity.ok(new HashMap<>());
        }

        logger.info("Authenticated user: {} ({})", oauth2User.getName(), oauth2User.getUserId());

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", oauth2User.getUserId());
        userDetails.put("name", oauth2User.getName());
        userDetails.put("email", oauth2User.getEmail());

        return ResponseEntity.ok(userDetails);
    }

    @GetMapping("/status")
    public ResponseEntity<String> getAuthStatus(Authentication authentication) {
        return ResponseEntity.ok(authentication != null ? "Authenticated" : "Not authenticated");
    }
}