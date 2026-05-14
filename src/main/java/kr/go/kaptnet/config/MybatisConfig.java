package kr.go.kaptnet.config;

import static kr.go.kaptnet.config.database.KapaDatabaseSource.KAPA_DATASOURCE;
import static kr.go.kaptnet.config.database.KapaDatabaseSource.KAPA_SQL_SESSION_FACTORY;

import java.util.Objects;
import javax.sql.DataSource;
import kr.go.kaptnet.config.database.JooqPackageConstants;
import kr.go.kaptnet.config.database.KapaDatabaseSource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;


/**
 * MyBatis 설정 클래스
 *
 * 이 클래스는 MyBatis를 사용하기 위한 설정을 제공합니다:
 * - SqlSessionFactory 생성 및 구성
 * - Mapper 스캔 및 등록
 * - Type Aliases 설정
 */
@Configuration
@Import(KapaDatabaseSource.class)
@MapperScan(
        basePackages = {"kr.go.kaptnet"},
        sqlSessionFactoryRef = KAPA_SQL_SESSION_FACTORY,
        annotationClass = Mapper.class
)
public class MybatisConfig {

    /**
     * MyBatis SqlSessionFactory 생성 및 구성
     *
     * @param storeDomainDataSource 플랫폼 데이터 소스
     * @param applicationContext Spring 애플리케이션 컨텍스트
     * @return 구성된 SqlSessionFactory
     * @throws Exception 설정 중 발생 가능한 예외
     */
    @Bean(KAPA_SQL_SESSION_FACTORY)
    public SqlSessionFactory kapaSqlSessionFactory(
            @Qualifier(KAPA_DATASOURCE) final DataSource storeDomainDataSource,
            final ApplicationContext applicationContext
    ) throws Exception {
        final SqlSessionFactoryBean factory = new SqlSessionFactoryBean();

        factory.setDataSource(storeDomainDataSource);
        factory.setVfs(SpringBootVFS.class);
        factory.setTypeAliasesPackage(JooqPackageConstants.MYBATIS_TYPE_ALIASES_PACKAGE);
        factory.setConfigLocation(applicationContext.getResource("classpath:mybatis-config.xml"));
        factory.setMapperLocations(
                applicationContext.getResources("classpath:mybatis-mapper/**/*.xml")
        );

        Objects.requireNonNull(factory.getObject())
                .getConfiguration()
                .setMapUnderscoreToCamelCase(true);

        return factory.getObject();
    }
}