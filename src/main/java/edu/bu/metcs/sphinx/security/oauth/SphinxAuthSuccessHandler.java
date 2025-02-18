package edu.bu.metcs.sphinx.security.oauth;

import edu.bu.metcs.sphinx.controller.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SphinxAuthSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(SphinxAuthSuccessHandler.class);

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        logger.info("Authentication success handler called");

        if (!(authentication.getPrincipal() instanceof SphinxOAuth2User oauthUser)) {
            logger.error("Unexpected principal type: {}",
                    authentication.getPrincipal().getClass().getName());
            response.sendRedirect(frontendUrl + "/auth-error");
            return;
        }

        logger.info("User authenticated: {} ({})", oauthUser.getName(), oauthUser.getUserId());

        // Session handling
        HttpSession session = request.getSession(true);
        logger.info("Session ID: {}", session.getId());

        session.setAttribute("userId", oauthUser.getUserId());
        session.setAttribute("userEmail", oauthUser.getEmail());
        session.setAttribute("userName", oauthUser.getName());
        logger.info("Session attributes set");

        // Add security context to session
        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.info("Security context set");

        logger.info("Redirecting to: {}", frontendUrl);
        response.sendRedirect(frontendUrl);
    }
}
