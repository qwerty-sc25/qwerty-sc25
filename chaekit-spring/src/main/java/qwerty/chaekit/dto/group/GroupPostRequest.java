package qwerty.chaekit.dto.group;

import java.util.List;

public record GroupPostRequest(
        String name,
        String description,
        List<String> tags
) { }
