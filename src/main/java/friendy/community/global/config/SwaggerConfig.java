package friendy.community.global.config;

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
    private String devUrl; // 서버 URL 값을 주입받을 변수

    @Bean
    public OpenAPI openAPI() {
        // Server 객체 생성 후 URL과 설명 설정
        final Server server = new Server();
        server.setUrl(devUrl); // devUrl을 사용하여 서버 URL 설정
        server.setDescription(SERVER_DESCRIPTION);

        // API 문서 정보를 설정
        final Info info = new Info()
            .title(DOCS_TITLE)
            .version(DOCS_VERSION)
            .description(DOCS_DESCRIPTION);

        // OpenAPI 객체 생성하여 서버와 문서 정보를 설정
        return new OpenAPI()
            .info(info)
            .servers(List.of(server)); // 서버 URL 설정
    }
}
