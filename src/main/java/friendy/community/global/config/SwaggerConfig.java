package friendy.community.global.config;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String SERVER_DESCRIPTION = "Friendy API";
    private static final String DOCS_TITLE = "Friendy API";
    private static final String DOCS_VERSION = "v1.0.0";
    private static final String DOCS_DESCRIPTION = "Friendy 프로젝트의 API 문서입니다.";
    private String devUrl;

    public void OpenApiConfig(@Value("${friendy.community.server.url}") final String dveUrl) {
        this.devUrl = dveUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        final Server server = new Server();
        server.setUrl(this.devUrl);
        server.setDescription(SERVER_DESCRIPTION);
        final Info info = new Info()
                .title(DOCS_TITLE)
                .version(DOCS_VERSION)
                .description(DOCS_DESCRIPTION);
        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
