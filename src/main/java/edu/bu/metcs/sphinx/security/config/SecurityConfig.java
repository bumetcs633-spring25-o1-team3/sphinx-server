package edu.bu.metcs.sphinx.security.config;

import edu.bu.metcs.sphinx.security.oauth.SphinxOAuth2UserService;
import edu.bu.metcs.sphinx.security.oauth.SphinxAuthSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final SphinxOAuth2UserService sphinxOAuth2UserService;
    private final SphinxAuthSuccessHandler sphinxAuthSuccessHandler;

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
                        .requestMatchers("/auth/**", "/error", "/login/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(sphinxOAuth2UserService)
                        )
                        .successHandler(sphinxAuthSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow requests from both development and production
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://sphinx-web.onrender.com"
        ));

        // Allow all common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Cache-Control", "Content-Type", "Origin",
                "X-Requested-With", "Accept"
        ));

        // This is critical - allow cookies to be sent in cross-origin requests
        configuration.setAllowCredentials(true);

        // Allow browsers to cache CORS response for 1 hour
        configuration.setMaxAge(3600L);

        // Expose headers that might be needed by the client
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization", "Content-Disposition"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
