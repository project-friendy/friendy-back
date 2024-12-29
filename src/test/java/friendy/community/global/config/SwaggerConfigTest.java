package friendy.community.global.config;

import friendy.community.global.swagger.SwaggerConfig;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;
    private static final String DEV_URL = "http://localhost:8080";
    private static final String SERVER_DESCRIPTION = "Friendy API";
    private static final String DOCS_TITLE = "Friendy API";
    private static final String DOCS_VERSION = "v1.0.0";
    private static final String DOCS_DESCRIPTION = "Friendy 프로젝트의 API 문서입니다.";

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
        swaggerConfig.OpenApiConfig(DEV_URL);
    }

    @Test
    @DisplayName("서버 설정이 올바르게 구성되어야 한다")
    void serverConfigurationIsCorrect() {
        OpenAPI openAPI = swaggerConfig.openAPI();

        List<Server> servers = openAPI.getServers();
        assertThat(servers).hasSize(1);

        Server server = servers.get(0);
        assertThat(server.getUrl()).isEqualTo(DEV_URL);
        assertThat(server.getDescription()).isEqualTo(SERVER_DESCRIPTION);
    }

    @Test
    @DisplayName("Info 설정이 올바르게 구성되어야 한다")
    void infoConfigurationIsCorrect() {
        OpenAPI openAPI = swaggerConfig.openAPI();

        Info info = openAPI.getInfo();
        assertThat(info.getTitle()).isEqualTo(DOCS_TITLE);
        assertThat(info.getVersion()).isEqualTo(DOCS_VERSION);
        assertThat(info.getDescription()).isEqualTo(DOCS_DESCRIPTION);
    }

}
