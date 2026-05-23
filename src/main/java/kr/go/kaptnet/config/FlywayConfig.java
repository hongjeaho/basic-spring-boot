package kr.go.kaptnet.config;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 수동 데이터베이스 마이그레이션 실행을 위한 Flyway 설정
 *
 * <p>이 설정은 애플리케이션 시작 시 자동 마이그레이션을 비활성화한 상태로
 * 프로그래밍 방식 마이그레이션 실행에 사용할 수 있는 Flyway 빈을 제공합니다.</p>
 *
 * <p>주요 설정:</p>
 * <ul>
 *   <li>마이그레이션 위치: classpath:db/migration</li>
 *   <li>마이그레이션 검증: true (마이그레이션 무결성 보장)</li>
 *   <li>시작 시 자동 마이그레이션: 비활성화 (수동 실행만)</li>
 * </ul>
 *
 * <p>수동 마이그레이션 실행 예시:</p>
 * <pre>{@code
 * @Autowired
 * private Flyway flyway;
 *
 * public void migrateDatabase() {
 *     flyway.migrate();
 * }
 * }</pre>
 */
@Configuration
public class FlywayConfig {

    private static final String KAPA_DATASOURCE = "kapaDataSource";

    private final DataSource kapaDataSource;

    public FlywayConfig(@Qualifier(KAPA_DATASOURCE) DataSource kapaDataSource) {
        this.kapaDataSource = kapaDataSource;
    }

    /**
     * 수동 마이그레이션 실행을 위해 설정된 Flyway 빈 생성
     *
     * <p>이 빈은 기존 KAPA_DATASOURCE를 사용하며, Flyway 문서의 최신 Spring Boot 3.x
     * 모범 사례를 따릅니다.</p>
     *
     * @return 설정된 Flyway 인스턴스
     */
    @Bean
    public Flyway flyway() {
        return FluentConfiguration
                .configure()
                .dataSource(kapaDataSource)
                .locations("classpath:db/migration")
                .validateOnMigrate(true)
                .placeholderReplacement(true)
                .load();
    }
}
