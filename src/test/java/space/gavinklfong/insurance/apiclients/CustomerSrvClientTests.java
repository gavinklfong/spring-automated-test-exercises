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
import space.gavinklfong.insurance.quotation.apiclients.CustomerSrvClient;
import space.gavinklfong.insurance.quotation.apiclients.CustomerSrvClientImpl;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;

import java.io.IOException;
import java.time.ZoneId;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
public class CustomerSrvClientTests {

    static final long DEFAULT_CUSTOMER_ID = 12L;
    private Faker faker = new Faker();
    ObjectMapper objectMapper = new ObjectMapper();

    public CustomerSrvClientTests() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void givenCustomerExists_whenRetrieveCustomer_thenSuccess(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        Customer mockedCustomer = Customer.builder()
                .id(DEFAULT_CUSTOMER_ID)
                .name(faker.name().name())
                .dob(faker.date().birthday().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .build();

        CustomerSrvClient customerSrvClient = new CustomerSrvClientImpl(wmRuntimeInfo.getHttpBaseUrl());

        // Given
        stubFor(get(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockedCustomer))
        ));

        // When
        List<Customer> customers = customerSrvClient.getCustomers(DEFAULT_CUSTOMER_ID);

        // Then
        verify(getRequestedFor(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID)));
        assertThat(customers).isNotEmpty().hasSize(1).containsExactlyInAnyOrder(mockedCustomer);
    }

    @Test
    void givenCustomerNotExists_whenRetrieveCustomer_thenFail(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        CustomerSrvClient customerSrvClient = new CustomerSrvClientImpl(wmRuntimeInfo.getHttpBaseUrl());

        // Given
        stubFor(get(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value()))
        );

        // When
        List<Customer> customers = customerSrvClient.getCustomers(DEFAULT_CUSTOMER_ID);

        // Then
        verify(
                getRequestedFor(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID))
        );
        assertThat(customers).isEmpty();
    }

}
