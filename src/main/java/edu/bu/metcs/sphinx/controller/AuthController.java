package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.model.User;
import edu.bu.metcs.sphinx.repository.UserRepository;
import edu.bu.metcs.sphinx.security.jwt.JwtUtil;
import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2User;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser(@AuthenticationPrincipal SphinxOAuth2User oauth2User) {
        if (oauth2User == null) {
            return ResponseEntity.ok(new HashMap<>());
        }

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", oauth2User.getUserId());
        userDetails.put("name", oauth2User.getName());
        userDetails.put("email", oauth2User.getEmail());

        return ResponseEntity.ok(userDetails);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        try {
            if (!jwtUtil.validateToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }

            UUID userId = jwtUtil.getUserIdFromToken(refreshToken);
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            User user = userOptional.get();

            // Create mock OAuth2User for token generation
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("sub", user.getEmail());
            attributes.put("name", user.getName());
            attributes.put("email", user.getEmail());

            // This is a simplified approach - in production you might use a different method
            SphinxOAuth2User sphinxUser = new SphinxOAuth2User(
                    null, // we'll handle this specially in the JwtUtil
                    userId
            );

            JwtUtil.TokenPair tokenPair = jwtUtil.generateTokenPair(sphinxUser);

            Map<String, String> response = new HashMap<>();
            response.put("accessToken", tokenPair.getAccessToken());
            response.put("refreshToken", tokenPair.getRefreshToken());

            return ResponseEntity.ok(response);

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error refreshing token");
        }
    }

    public static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}