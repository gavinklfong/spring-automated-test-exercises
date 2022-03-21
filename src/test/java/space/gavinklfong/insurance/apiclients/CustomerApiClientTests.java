package space.gavinklfong.insurance.apiclients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import space.gavinklfong.insurance.quotation.apiclients.CustomerApiClient;
import space.gavinklfong.insurance.quotation.apiclients.CustomerApiClientImpl;
import space.gavinklfong.insurance.quotation.apiclients.ProductApiClient;
import space.gavinklfong.insurance.quotation.apiclients.ProductApiClientImpl;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;

import java.io.IOException;
import java.time.LocalDate;
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
        CustomerApiClient customerApiClient = new CustomerApiClientImpl(wmRuntimeInfo.getHttpBaseUrl());

    }

    @Test
    void givenCustomerNotExists_whenRetrieveCustomer_thenFail(WireMockRuntimeInfo wmRuntimeInfo) throws IOException {
        CustomerApiClient customerApiClient = new CustomerApiClientImpl(wmRuntimeInfo.getHttpBaseUrl());

    }

}
