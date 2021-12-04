package component;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import space.gavinklfong.supermarket.SupermarketApplication;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
        initializers = { ComponentTestContextInitializer.class },
        classes = {SupermarketApplication.class, ComponentTestContextConfig.class}
)
@ActiveProfiles(profiles={"component-test"})
public abstract class AbstractComponentTest {
}
