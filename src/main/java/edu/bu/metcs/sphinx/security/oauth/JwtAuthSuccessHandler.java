package edu.bu.metcs.sphinx.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.bu.metcs.sphinx.security.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        SphinxOAuth2User oauthUser = (SphinxOAuth2User) authentication.getPrincipal();

        // Generate JWT token pair
        JwtUtil.TokenPair tokenPair = jwtUtil.generateTokenPair(authentication);

        // Create response with tokens and user data
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", tokenPair.getAccessToken());
        responseData.put("refreshToken", tokenPair.getRefreshToken());
        responseData.put("userId", oauthUser.getUserId());
        responseData.put("email", oauthUser.getEmail());
        responseData.put("name", oauthUser.getName());

        // Redirect with tokens in fragment
        String tokenFragment = "#access_token=" + tokenPair.getAccessToken() +
                "&refresh_token=" + tokenPair.getRefreshToken();

        response.sendRedirect(frontendUrl + "/auth-callback" + tokenFragment);
    }
}