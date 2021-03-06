package space.gavinklfong.insurance.apiclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import space.gavinklfong.insurance.quotation.apiclients.ProductApiClient;
import space.gavinklfong.insurance.quotation.apiclients.ProductApiClientImpl;
import space.gavinklfong.insurance.quotation.apiclients.dto.Product;

import java.io.IOException;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
public class ProductApiClientTests {

    static final private String PRODUCT_CODE = "CAR001-01";
    private final int HIGH_RISK_AGE = 70;
    private final double HIGH_RISK_AGE_ADJ_RATE = 1.5;
    private final double POST_CODE_DISCOUNT_RATE = 0.3;
    private final long LISTED_PRICE = 1500L;
    private final String[] PRODUCT_POST_CODE = {"SW20", "SM1", "E12" };

    ObjectMapper objectMapper = new ObjectMapper();

    private Faker faker = new Faker();

    public ProductApiClientTests() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void givenProductExists_whenRetrieveProduct_thenSuccess(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        ProductApiClient productSrvClient = new ProductApiClientImpl(wmRuntimeInfo.getHttpBaseUrl());

        // Given
        Product mockedProduct = Product.builder()
                .id(PRODUCT_CODE)
                .buildingSumInsured(faker.number().randomNumber())
                .contentSumInsured(faker.number().randomNumber())
                .customerAgeThreshold(HIGH_RISK_AGE)
                .customerAgeThresholdAdjustmentRate(HIGH_RISK_AGE_ADJ_RATE)
                .discountPostCode(PRODUCT_POST_CODE)
                .postCodeDiscountRate(POST_CODE_DISCOUNT_RATE)
                .listedPrice(LISTED_PRICE)
                .build();

        stubFor(get(urlEqualTo("/products/" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockedProduct)))
        );

        // When
        Optional<Product> product = productSrvClient.getProductByCode(PRODUCT_CODE);

        // Then
        verify(getRequestedFor(urlEqualTo("/products/" + PRODUCT_CODE)));
        assertThat(product)
                .isPresent()
                .hasValue(mockedProduct);
    }

    @Test
    void givenProductNotExists_whenRetrieveProduct_thenFail(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        ProductApiClient productSrvClient = new ProductApiClientImpl(wmRuntimeInfo.getHttpBaseUrl());

        // Given
        stubFor(get(urlEqualTo("/products/" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value()))
        );

        // When
        Optional<Product> product = productSrvClient.getProductByCode(PRODUCT_CODE);

        // Then
        verify(getRequestedFor(urlEqualTo("/products/" + PRODUCT_CODE)));
        assertThat(product).isEmpty();
    }

}
