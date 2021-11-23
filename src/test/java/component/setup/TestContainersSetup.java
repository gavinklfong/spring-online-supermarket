package component.setup;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public class TestContainersSetup {

    private static final int ELASTICSEARCH_PORT = 9200;
    private static final DockerImageName ELASTICSEARCH_IMAGE = DockerImageName.parse("elasticsearch").withTag("7.14.2");
    private static final Logger ELASTICSEARCH_LOGGER = LoggerFactory.getLogger("container.Elasticsearch");

    private static final ElasticsearchContainer esContainer = new ElasticsearchContainer(ELASTICSEARCH_IMAGE)
            .withExposedPorts(ELASTICSEARCH_PORT);

    static public void initTestContainers(ConfigurableEnvironment configEnv) {
        esContainer.start();
        esContainer.followOutput(new Slf4jLogConsumer(ELASTICSEARCH_LOGGER));
    }

    public static String getElasticsearchIPAddress() {
        return esContainer.getContainerIpAddress();
    }

    public static int getElasticsearchPort() {
        return esContainer.getMappedPort(ELASTICSEARCH_PORT);
    }
}
