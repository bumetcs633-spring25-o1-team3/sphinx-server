package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.repository.UserRepository;
import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            Authentication authentication,
            HttpServletRequest request) {

        // Log incoming request details
        logger.info("Auth request received from: {}", request.getHeader("Origin"));
        logger.info("Request cookies: {}", Arrays.toString(request.getCookies()));

        Map<String, Object> response = new HashMap<>();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof SphinxOAuth2User) {
                SphinxOAuth2User user = (SphinxOAuth2User) principal;
                logger.info("User found in authentication: {}", user.getEmail());

                response.put("id", user.getUserId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("authenticated", true);

                return ResponseEntity.ok(response);
            }

            logger.warn("Principal is not SphinxOAuth2User: {}", principal.getClass().getName());
        } else {
            logger.warn("No authentication found in request");
        }

        // Return an empty response for unauthenticated users
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session-test")
    public ResponseEntity<Map<String, Object>> testSession(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        HttpSession session = request.getSession(false);

        if (session != null) {
            response.put("sessionId", session.getId().substring(0, 8) + "...");
            response.put("creationTime", new Date(session.getCreationTime()));
            response.put("lastAccessedTime", new Date(session.getLastAccessedTime()));
            response.put("maxInactiveInterval", session.getMaxInactiveInterval());

            // Get all attribute names
            Enumeration<String> attributeNames = session.getAttributeNames();
            List<String> attributes = new ArrayList<>();
            while (attributeNames.hasMoreElements()) {
                attributes.add(attributeNames.nextElement());
            }
            response.put("attributes", attributes);

            return ResponseEntity.ok(response);
        }

        response.put("session", "not found");
        return ResponseEntity.ok(response);
    }
}