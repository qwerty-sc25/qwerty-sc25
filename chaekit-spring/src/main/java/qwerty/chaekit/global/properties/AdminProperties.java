package qwerty.chaekit.global.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "admin")
public record AdminProperties(
        String name,
        String email,
        String password
) {}