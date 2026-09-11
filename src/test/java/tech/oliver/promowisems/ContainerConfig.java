package tech.oliver.promowisems;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Map;

@Testcontainers
public class ContainerConfig {

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("docker pull wiremock/wiremock:3.13.2");

    public static Map<String, String> getProperties() {
        return Map.of(
                "partner.api.url", "",
                "telemetre.api.url", ""
        );
    }

    @DynamicPropertySource
    public void dynamicPropertySource(DynamicPropertyRegistry registry) {
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getPort());

        getProperties().entrySet().forEach(kv -> {
            registry.add(kv.getKey(), kv:: getValue);
        });
    }

    public String getWireMockUrl() {
        return "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getFirstMappedPort();
    }
}
