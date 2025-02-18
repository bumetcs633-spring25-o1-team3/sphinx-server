package edu.bu.metcs.sphinx.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@Configuration
public class SessionConfig {

    @Value("${app.cookie.secure:true}")
    private boolean secureCookie;

    @Value("${app.cookie.same-site:none}")
    private String sameSite;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();

        // Set the cookie name
        serializer.setCookieName("SPHINX_SESSION");

        // Use SameSite=None for cross-origin requests
        serializer.setSameSite(sameSite);

        // Set secure flag for HTTPS connections
        serializer.setUseSecureCookie(secureCookie);

        // Allow cookies to be sent in cross-site requests
        serializer.setCookiePath("/");

        return serializer;
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return new CookieHttpSessionIdResolver();
    }
}
