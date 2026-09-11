package tech.oliver.promowisems;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.util.Map;

@Testcontainers
public class ContainerConfig {

    @Container
    static WireMockContainer wireMockContainer =
            new WireMockContainer("wiremock/wiremock:3.13.2")
                    .withCopyToContainer(MountableFile.forClasspathResource("wiremock"), "/home/wiremock");

    public static Map<String, String> getProperties() {
        return Map.of(
                "partner.api.url", getWireMockUrl(),
                "telemetry.api.url", getWireMockUrl()
        );
    }

    @DynamicPropertySource
    public void dynamicPropertySource(DynamicPropertyRegistry registry) {
        WireMock.configureFor(wireMockContainer.getHost(), wireMockContainer.getPort());

        getProperties().entrySet().forEach(kv -> {
            registry.add(kv.getKey(), kv:: getValue);
        });
    }

    public static String getWireMockUrl() {
        return "http://" + wireMockContainer.getHost() + ":" + wireMockContainer.getFirstMappedPort();
    }
}
