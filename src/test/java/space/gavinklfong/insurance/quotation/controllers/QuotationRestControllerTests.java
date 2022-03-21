package space.gavinklfong.insurance.quotation.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import space.gavinklfong.insurance.quotation.dto.QuotationReq;
import space.gavinklfong.insurance.quotation.exceptions.RecordNotFoundException;
import space.gavinklfong.insurance.quotation.models.Quotation;
import space.gavinklfong.insurance.quotation.services.QuotationService;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {QuotationRestController.class})
public class QuotationRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuotationService quotationService;

    private ObjectMapper mapper = new ObjectMapper();

    private Faker faker = new Faker();

    @Test
    void givenSuccessfulQuotationGeneration_whenPostForQuotation_thenReturnQuotation() throws Exception {

    }

    @Test
    void givenRecordNotFound_whenPostForQuotation_thenReturnBadRequest() throws Exception {

    }

    @ParameterizedTest
    @MethodSource("generateInvalidQuotationRequests")
    void givenInvalidRequest_whenPostForQuotation_thenReturnBadRequest(QuotationReq quotationReq) throws Exception {

    }

    static Stream<Arguments> generateInvalidQuotationRequests() {
        return Stream.of(
                Arguments.of(QuotationReq.builder().build()),
                Arguments.of(QuotationReq.builder().postCode("ABC").build()),
                Arguments.of(QuotationReq.builder().customerId(1L).build()),
                Arguments.of(QuotationReq.builder().customerId(1L).postCode("ABC").build()),
                Arguments.of(QuotationReq.builder().productCode("HOME-001").build()),
                Arguments.of(QuotationReq.builder().productCode("HOME-001").postCode("ABC").build())
        );
    }


}
