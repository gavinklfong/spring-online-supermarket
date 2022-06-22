package space.gavinklfong.supermarket.repositories;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@ActiveProfiles("test")
public class CassandraRepositoryBaseTest {

    private static final String KEYSPACE = "supermarket";

    public static final GenericContainer container =
            new CassandraContainer("cassandra")
            .withInitScript("cassandra_init.cql")
            .withReuse(true);

    static {
        container.start();
    }

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.keyspace-name", () -> KEYSPACE);
        registry.add("spring.data.cassandra.contact-points", () -> container.getContainerIpAddress());
        registry.add("spring.data.cassandra.port", () -> container.getMappedPort(9042));
    }

}
