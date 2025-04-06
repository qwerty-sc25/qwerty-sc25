package qwerty.chaekit.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("springdoc-public")
                .pathsToMatch("/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Chaekit API")
                        .version("1.0")
                        .description("책잇 API 명세서"));
    }

    @Bean
    public GroupedOpenApi hideLoginMemberParams() {
        return GroupedOpenApi.builder()
                .group("springdoc-hidden")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    if (operation.getParameters() != null) {
                        operation.getParameters().removeIf(param -> param.getName().equals("loginMember"));
                    }
                    return operation;
                })
                .pathsToMatch("/**") // 모든 경로에 대해 적용
                .build();
    }
}