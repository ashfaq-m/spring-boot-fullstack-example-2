package com.amigoscode;

import com.github.javafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestContainerUnitTest {

    protected static final Faker FAKER = new Faker();

    @BeforeAll
    static void beforeAll() {
        Flyway flyway = Flyway.configure().dataSource(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getUsername(),
                postgreSQLContainer.getPassword()
        ).load();
        flyway.migrate();
        System.out.println();
    }

    @Container
    protected static final PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("amigoscode-dao-unit-test")
                    .withUsername("amigoscode")
                    .withPassword("password");

    @DynamicPropertySource
    private static void registerDataSourceProperties(DynamicPropertyRegistry registery){
        registery.add(
                "spring.datasource.url",
                postgreSQLContainer::getJdbcUrl
        );
        registery.add(
                "spring.datasource.username",
                postgreSQLContainer::getUsername
        );
        registery.add(
                "spring.datasource.password",
                postgreSQLContainer::getPassword
        );
    }

    private static DataSource getDataSource() {
//        DataSourceBuilder builder = DataSourceBuilder.create()
//                .driverClassName(postgreSQLContainer.getDriverClassName())
//                .url(postgreSQLContainer.getJdbcUrl())
//                .username(postgreSQLContainer.getUsername())
//                .password(postgreSQLContainer.getPassword());
//
//        return builder.build();

      return DataSourceBuilder.create()
                .driverClassName(postgreSQLContainer.getDriverClassName())
                .url(postgreSQLContainer.getJdbcUrl())
                .username(postgreSQLContainer.getUsername())
                .password(postgreSQLContainer.getPassword())
                .build();

    }

    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }
}
