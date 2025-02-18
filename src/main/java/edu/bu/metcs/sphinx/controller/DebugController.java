package edu.bu.metcs.sphinx.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/debug")
public class DebugController {

    @Autowired
    private Environment env;

    @GetMapping("/auth-status")
    public Map<String, Object> getAuthStatus(HttpServletRequest request,
                                             @AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> debugInfo = new HashMap<>();

        // Session info
        debugInfo.put("sessionId", request.getSession().getId());
        debugInfo.put("sessionCreationTime", new Date(request.getSession().getCreationTime()));
        debugInfo.put("sessionAttributes", getSessionAttributes(request.getSession()));

        // Auth info
        debugInfo.put("isAuthenticated", principal != null);
        debugInfo.put("authDetails", principal != null ?
                Map.of("name", principal.getName(),
                        "authorities", principal.getAuthorities()) : null);

        // Request info
        debugInfo.put("requestHeaders", getHeadersMap(request));

        return debugInfo;
    }

    @GetMapping("/environment")
    public Map<String, String> getEnvironment() {
        Map<String, String> envInfo = new HashMap<>();
        envInfo.put("activeProfiles", String.join(", ", env.getActiveProfiles()));
        envInfo.put("frontendUrl", env.getProperty("app.frontend.url"));
        envInfo.put("serverPort", env.getProperty("server.port"));
        envInfo.put("dataSourceUrl", maskSensitiveInfo(env.getProperty("spring.datasource.url")));
        // Don't include credentials
        return envInfo;
    }

    private String maskSensitiveInfo(String input) {
        if (input == null) return null;
        return input.replaceAll("password=.*?(&|$)", "password=*****$1");
    }

    private Map<String, Object> getSessionAttributes(HttpSession session) {
        Map<String, Object> attributes = new HashMap<>();
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            attributes.put(name, session.getAttribute(name));
        }
        return attributes;
    }

    private Map<String, String> getHeadersMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}
