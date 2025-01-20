package friendy.community.global.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.In;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private static final String SERVER_DESCRIPTION = "Friendy API";
    private static final String DOCS_TITLE = "Friendy API";
    private static final String DOCS_VERSION = "v1.0.0";
    private static final String DOCS_DESCRIPTION = "Friendy 프로젝트의 API 문서입니다.";

    @Value("${friendy.community.server.url}")
    private String devUrl;

    @Bean
    public OpenAPI openAPI() {
        final Server server = new Server();
        server.setUrl(devUrl);
        server.setDescription(SERVER_DESCRIPTION);

        final Info info = new Info()
                .title(DOCS_TITLE)
                .version(DOCS_VERSION)
                .description(DOCS_DESCRIPTION);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server))
                .components(generateComponents())
                .security(List.of(generateSecurityRequirement()));
    }

    private Components generateComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", generateAccessTokenScheme())
                .addSecuritySchemes("refreshAuth", generateRefreshTokenScheme());
    }

    private SecurityRequirement generateSecurityRequirement() {
        return new SecurityRequirement()
                .addList("bearerAuth")
                .addList("refreshAuth");
    }

    private SecurityScheme generateAccessTokenScheme() {
        return new SecurityScheme()
                .type(Type.APIKEY)
                .in(In.HEADER)
                .name("Authorization")
                .description("Access Token");
    }

    private SecurityScheme generateRefreshTokenScheme() {
        return new SecurityScheme()
                .type(Type.APIKEY)
                .in(In.HEADER)
                .name("Authorization-Refresh")
                .description("Refresh Token");
    }

}
