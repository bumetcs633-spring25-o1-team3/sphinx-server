package edu.bu.metcs.sphinx.controller;

import edu.bu.metcs.sphinx.dto.UserDTO;
import edu.bu.metcs.sphinx.model.User;
import edu.bu.metcs.sphinx.repository.UserRepository;
import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok().build();
        }

        try {
            if (authentication.getPrincipal() instanceof SphinxOAuth2User oauth2User) {
                UUID userId = oauth2User.getUserId();

                var userTest = userRepository.findById(userId);

                if (userTest.isPresent()) {
                    User user = userTest.get();
                    UserDTO dto = new UserDTO();
                    dto.setId(user.getId());
                    dto.setEmail(user.getEmail());
                    dto.setName(user.getName());
                    return ResponseEntity.ok(dto);
                }

            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}