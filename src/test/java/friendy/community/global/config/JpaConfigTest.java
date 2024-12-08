package friendy.community.global.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JpaConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("JPA Auditing이 활성화되어 있는지 확인")
    void jpaAuditingIsEnabled() {
        boolean isJpaAuditingEnabled = applicationContext.getBeansWithAnnotation(EnableJpaAuditing.class).size() > 0;
        assertThat(isJpaAuditingEnabled).isTrue();
    }
}