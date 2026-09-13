package tech.oliver.promowisems.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import tech.oliver.promowisems.ContainerConfig;
import tech.oliver.promowisems.ServiceConnectionConfig;
import tech.oliver.promowisems.entity.Coupon;
import tech.oliver.promowisems.repository.CouponHistoryRepository;
import tech.oliver.promowisems.repository.CouponRepository;

import java.time.LocalDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.matching.RequestPatternBuilder.newRequestPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Import(ServiceConnectionConfig.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponControllerIT extends ContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private CouponHistoryRepository couponHistoryRepository;

    @BeforeEach
    public void beforeEach() {
        WireMock.resetAllRequests();
        couponRepository.deleteAll();
        couponHistoryRepository.deleteAll();
    }

    @Nested
    class validate {

        @Nested
        class validCouponScenarios {

            String couponCode = "PROMO50";
            int discountPercentage = 50;
            int remainingUsages = 10;
            LocalDateTime validUntil = LocalDateTime.now().plusDays(7);
            private ResultActions setupArrangeAct() throws Exception {

                couponRepository.save(new Coupon(couponCode, discountPercentage, remainingUsages, validUntil));

                var result = mockMvc.perform(
                        post("/api/v1/coupons/validate")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(String.format("""
                                            {
                                                "couponCode": "%s"
                                            }
                                        """, couponCode))
                );
                return result;
            }

            @Test
            void shouldReturnCorrectApiBody() throws Exception {
                var result = setupArrangeAct();

                result.andExpect(status().is(200))
                        .andExpect(jsonPath("$.couponCode").value(couponCode))
                        .andExpect(jsonPath("$.valid").value(true))
                        .andExpect(jsonPath("$.discountPercentage").value(discountPercentage))
                        .andExpect(jsonPath("$.validatedAt").isNotEmpty())
                        .andExpect(jsonPath("$.remainingUses").value(remainingUsages - 1))
                        .andExpect(jsonPath("$.message").value("Cupom válido! Aproveite seu desconto."));
            }

            @Test
            void shouldCallExternalApi() throws Exception {
                setupArrangeAct();

                WireMock.verify(1, newRequestPattern()
                        .withUrl("/api/partners/v1/coupons/status")
                        .withHeader("x-api-key", equalTo("valid-api-key"))
                        .withRequestBody(equalToJson(String.format("""
                                {
                                    "couponCode": "%s"
                                }
                                """, couponCode)))
                );
            }

            @Test
            void shouldDecreaseCouponUsageOnDatabase() throws Exception {
                setupArrangeAct();

                var coupon = couponRepository.findById(couponCode);
                assertTrue(coupon.isPresent());
                assertEquals(remainingUsages - 1, coupon.get().getRemainingUses());
            }

            @Test
            void shouldInsertToHistoryTable() throws Exception {
                setupArrangeAct();

                var couponHistory = couponHistoryRepository.findByCouponCodeOrderByValidatedAtDesc(couponCode);
                assertEquals(1, couponHistory.size());
                assertNotNull(couponHistory.getFirst().getId());
                assertNotNull(couponHistory.getFirst().getValidatedAt());
                assertEquals(couponCode, couponHistory.getFirst().getCouponCode());
                assertEquals(discountPercentage, couponHistory.getFirst().getDiscountPercentage());
            }
        }

        @Nested
        class invalidCouponScenarios {

            String notFoundCouponCode = "NOTFOUND";
            String expiredCouponCode = "EXPIRED50";
            int discountPercentage = 0;
            int remainingUsages = 0;
            LocalDateTime validUntil = LocalDateTime.now().minusDays(7);
            String invalidMessage = "Cupom inválido, expirado ou sem usos restantes.";

            private ResultActions setupArrangeAct(String coupon) throws Exception {

                couponRepository.save(new Coupon(coupon, discountPercentage, remainingUsages, validUntil));

                var result = mockMvc.perform(
                        post("/api/v1/coupons/validate")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(String.format("""
                                            {
                                                "couponCode": "%s"
                                            }
                                        """, coupon))
                );
                return result;
            }

            @Test
            void whenCouponNotFoundOnDatabaseShouldReturn404() throws Exception {

                var result = mockMvc.perform(
                        post("/api/v1/coupons/validate")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(String.format("""
                                            {
                                                "couponCode": "%s"
                                            }
                                        """, notFoundCouponCode))
                );

                var resp = result.andExpect(status().is(404))
                        .andReturn();
                assertTrue(resp.getResponse().getContentAsString().isBlank());
            }

            @Test
            void whenExpiredOnPartnerShouldReturnCorrectApiBody() throws Exception {
                var result = setupArrangeAct(expiredCouponCode);

                result.andExpect(status().is(200))
                        .andExpect(jsonPath("$.couponCode").value(expiredCouponCode))
                        .andExpect(jsonPath("$.valid").value(false))
                        .andExpect(jsonPath("$.discountPercentage").value(discountPercentage))
                        .andExpect(jsonPath("$.validatedAt").isNotEmpty())
                        .andExpect(jsonPath("$.remainingUses").value(remainingUsages))
                        .andExpect(jsonPath("$.message").value(invalidMessage));
            }

            @Test
            void whenExpiredOnPartnerShouldCallPartnerApi() throws Exception {
                setupArrangeAct(expiredCouponCode);

                WireMock.verify(1, newRequestPattern()
                        .withUrl("/api/partners/v1/coupons/status")
                        .withHeader("x-api-key", equalTo("valid-api-key"))
                        .withRequestBody(equalToJson(String.format("""
                                {
                                    "couponCode": "%s"
                                }
                                """, expiredCouponCode)))
                );
            }

            @Test
            void whenNotFoundOnPartnerShouldReturnCorrectApiBody() throws Exception {
                var result = setupArrangeAct(notFoundCouponCode);

                result.andExpect(status().is(200))
                        .andExpect(jsonPath("$.couponCode").value(notFoundCouponCode))
                        .andExpect(jsonPath("$.valid").value(false))
                        .andExpect(jsonPath("$.discountPercentage").value(discountPercentage))
                        .andExpect(jsonPath("$.validatedAt").isNotEmpty())
                        .andExpect(jsonPath("$.remainingUses").value(remainingUsages))
                        .andExpect(jsonPath("$.message").value(invalidMessage));
            }

            @Test
            void whenNotFoundOnPartnerShouldCallPartnerApi() throws Exception {
                setupArrangeAct(notFoundCouponCode);

                WireMock.verify(1, newRequestPattern()
                        .withUrl("/api/partners/v1/coupons/status")
                        .withHeader("x-api-key", equalTo("valid-api-key"))
                        .withRequestBody(equalToJson(String.format("""
                                {
                                    "couponCode": "%s"
                                }
                                """, notFoundCouponCode)))
                );
            }
        }
    }
}



