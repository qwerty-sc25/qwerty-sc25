package qwerty.chaekit.domain.Member.enums;

import java.util.Arrays;

public enum Role {
    ROLE_USER,
    ROLE_PUBLISHER,
    ROLE_ADMIN;

    public static Role from(String value) {
        return Arrays.stream(values())
                .filter(role -> role.name().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown role: " + value));
    }
}