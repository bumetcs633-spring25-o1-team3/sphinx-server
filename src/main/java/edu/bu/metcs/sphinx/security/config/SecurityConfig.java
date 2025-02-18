package edu.bu.metcs.sphinx.security.config;

import edu.bu.metcs.sphinx.controller.AuthController;
import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2UserService;
import edu.bu.metcs.sphinx.security.oauth.SphinxAuthSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final SphinxOAuth2UserService sphinxOAuth2UserService;
    private final SphinxAuthSuccessHandler sphinxAuthSuccessHandler;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Autowired
    public SecurityConfig(SphinxOAuth2UserService sphinxOAuth2UserService,
                          SphinxAuthSuccessHandler sphinxAuthSuccessHandler) {
        this.sphinxOAuth2UserService = sphinxOAuth2UserService;
        this.sphinxAuthSuccessHandler = sphinxAuthSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/error", "/login/**", "/debug/**").permitAll() // Add debug endpoint
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(sphinxOAuth2UserService)
                        )
                        .successHandler(sphinxAuthSuccessHandler)
                        .failureHandler((request, response, exception) -> {
                            logger.error("Authentication failure: {}", exception.getMessage(), exception);
                            response.sendRedirect(frontendUrl + "/auth-error?error=" + exception.getMessage());
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .maximumSessions(1)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(frontendUrl)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://sphinx-web.onrender.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true); // Critical for auth cookies
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
