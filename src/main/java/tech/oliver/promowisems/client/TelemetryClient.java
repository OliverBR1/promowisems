package tech.oliver.promowisems.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import tech.oliver.promowisems.client.dto.TelemetryRequest;

@FeignClient(name = "telemetryClient", url = "${telemetry.api.url}")
public interface TelemetryClient {

    @PostMapping("/v1/events")
    void sendEvent(@RequestHeader("x-api-key") String apiKey, TelemetryRequest request);

}
