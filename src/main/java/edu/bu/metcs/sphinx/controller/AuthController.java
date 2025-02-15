package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

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
}