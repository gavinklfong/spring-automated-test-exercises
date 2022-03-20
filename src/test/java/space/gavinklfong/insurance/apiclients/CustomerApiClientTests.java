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
import space.gavinklfong.insurance.quotation.apiclients.CustomerApiClient;
import space.gavinklfong.insurance.quotation.apiclients.CustomerApiClientImpl;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@WireMockTest
public class CustomerApiClientTests {

    static final long DEFAULT_CUSTOMER_ID = 12L;
    ObjectMapper objectMapper = new ObjectMapper();

    public CustomerApiClientTests() {
        super();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void givenCustomerExists_whenRetrieveCustomer_thenReturnCustomer(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        // Given
        Customer mockedCustomer = Customer.builder()
                .id(DEFAULT_CUSTOMER_ID)
                .name("John Don")
                .dob(LocalDate.of(2000, 2, 15))
                .build();

        CustomerApiClient customerSrvClient = new CustomerApiClientImpl(wmRuntimeInfo.getHttpBaseUrl());

        stubFor(get(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(mockedCustomer))
        ));

        // When
        Optional<Customer> customer = customerSrvClient.getCustomerById(DEFAULT_CUSTOMER_ID);

        // Then
        verify(getRequestedFor(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID)));
        assertThat(customer)
                .isPresent()
                .hasValue(mockedCustomer);
    }

    @Test
    void givenCustomerNotExists_whenRetrieveCustomer_thenFail(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {

        // Given
        CustomerApiClient customerSrvClient = new CustomerApiClientImpl(wmRuntimeInfo.getHttpBaseUrl());

        stubFor(get(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value()))
        );

        // When
        Optional<Customer> customer = customerSrvClient.getCustomerById(DEFAULT_CUSTOMER_ID);

        // Then
        verify(
                getRequestedFor(urlEqualTo("/customers/" + DEFAULT_CUSTOMER_ID))
        );
        assertThat(customer).isEmpty();
    }

}
