package space.gavinklfong.supermarket.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class CassandraRepositoryBaseTest {

    private static final String KEYSPACE = "supermarket";
    @Container
    public static CassandraContainer container = new CassandraContainer("cassandra").withInitScript("cassandra_init.cql");

    @DynamicPropertySource
    static void dataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.cassandra.keyspace-name", () -> KEYSPACE);
        registry.add("spring.data.cassandra.contact-points", () -> container.getContainerIpAddress());
        registry.add("spring.data.cassandra.port", () -> container.getMappedPort(9042));
    }

    //    @BeforeAll
//    static void createKeyspace() {
//        try (Session session = container.getCluster().connect()) {
//            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE + " WITH replication = " +
//                    "{'class':'SimpleStrategy','replication_factor':'1'};");
//        }
//    }
}
