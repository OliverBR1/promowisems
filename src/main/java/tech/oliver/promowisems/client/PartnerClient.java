package tech.oliver.promowisems.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import tech.oliver.promowisems.client.dto.PartnerCouponStatusRequest;
import tech.oliver.promowisems.client.dto.PartnerCouponStatusResponse;

@FeignClient(name = "partnerClient", url = "${partner.api.url}")
public interface PartnerClient {

    @PostMapping(value = "/api/partners/v1/coupons/status", consumes = "application/json", produces = "application/json")
    PartnerCouponStatusResponse getCouponStatus(@RequestHeader("x-api-key") String apiKey,
                                                PartnerCouponStatusRequest request);

}
