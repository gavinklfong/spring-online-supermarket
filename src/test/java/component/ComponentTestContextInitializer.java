package component;

import component.setup.TestContainersSetup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import static component.setup.TestContainersSetup.getElasticsearchIPAddress;
import static component.setup.TestContainersSetup.getElasticsearchPort;

@Slf4j
public class ComponentTestContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        // start up and initialize test containers
        TestContainersSetup.initTestContainers(configurableApplicationContext.getEnvironment());

        // alter spring boot system properties so that it will connect to test containers and wiremock
        TestPropertyValues values = TestPropertyValues.of(
                "spring.elasticsearch.uris=http://" + getElasticsearchIPAddress() + ":" + getElasticsearchPort(),
                "spring.data.elasticsearch.client.reactive.endpoints=" + getElasticsearchIPAddress() + ":" + getElasticsearchPort()
        );

        values.applyTo(configurableApplicationContext);

        log.info("======= Customized properties settings =======");
        log.info("spring.elasticsearch.uris=http://" + getElasticsearchIPAddress() + ":" + getElasticsearchPort());
        log.info("spring.data.elasticsearch.client.reactive.endpoints=http://" + getElasticsearchIPAddress() + ":" + getElasticsearchPort());
        log.info("==============");

    }
}
