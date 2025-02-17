package edu.bu.metcs.sphinx.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SphinxAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        SphinxOAuth2User oauthUser = (SphinxOAuth2User) authentication.getPrincipal();

        // Add any necessary session attributes
        HttpSession session = request.getSession();
        session.setAttribute("userId", oauthUser.getUserId());
        session.setAttribute("userEmail", oauthUser.getEmail());
        session.setAttribute("userName", oauthUser.getName());

        // Redirect to home
        response.sendRedirect(frontendUrl);
    }
}
