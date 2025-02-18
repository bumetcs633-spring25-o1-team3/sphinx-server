package edu.bu.metcs.sphinx.security.oauth;

import edu.bu.metcs.sphinx.controller.AuthController;
import edu.bu.metcs.sphinx.model.User;
import edu.bu.metcs.sphinx.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SphinxOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(SphinxOAuth2UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        logger.info("OAuth2 authentication request received from: {}",
                userRequest.getClientRegistration().getRegistrationId());

        OAuth2User oauth2User = super.loadUser(userRequest);
        logger.info("OAuth2 user loaded: {}", oauth2User.getName());

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (Exception ex) {
            logger.error("Error processing OAuth2 user: {}", ex.getMessage(), ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // Extract OAuth2 provider (e.g., "google")
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // Get email from OAuth2 user info
        String email = oauth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // Find or create user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(oauth2User.getAttribute("name"));
                    return userRepository.save(newUser);
                });

        return new SphinxOAuth2User(oauth2User, user.getId());
    }
}
