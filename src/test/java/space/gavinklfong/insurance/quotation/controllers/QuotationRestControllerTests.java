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

        final Quotation QUOTATION = Quotation.builder()
                .quotationCode(UUID.randomUUID().toString())
                .amount(faker.number().randomDouble(2, 1000, 5000))
                .expiryTime(LocalDateTime.now().plusMinutes(10).withNano(0))
                .build();

        QuotationReq quotationReq = QuotationReq.builder()
                .postCode(faker.address().zipCode())
                .customerId(faker.number().randomNumber())
                .productCode(faker.code().toString())
                .build();

        when(quotationService.generateQuotation(quotationReq))
                .thenAnswer(invocation -> {
                    QuotationReq req = (QuotationReq) invocation.getArgument(0);
                    return QUOTATION
                            .withProductCode(req.getProductCode())
                            .withCustomerId(req.getCustomerId());
                }
        );

        mockMvc.perform(
                post("/quotations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(quotationReq))
        )
                .andDo((print()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quotationCode").value(QUOTATION.getQuotationCode()))
                .andExpect(jsonPath("$.amount").value(QUOTATION.getAmount()))
                .andExpect(jsonPath("$.expiryTime").value(QUOTATION.getExpiryTime().toString()))
                .andExpect(jsonPath("$.productCode").value(quotationReq.getProductCode()))
                .andExpect(jsonPath("$.customerId").value(quotationReq.getCustomerId()));
    }

    @Test
    void givenRecordNotFound_whenPostForQuotation_thenReturnBadRequest() throws Exception {

        when(quotationService.generateQuotation(any(QuotationReq.class))).thenThrow(new RecordNotFoundException());

        QuotationReq req = QuotationReq.builder()
                .postCode(faker.address().zipCode())
                .customerId(faker.number().randomNumber())
                .productCode(faker.code().toString())
                .build();

        mockMvc.perform(
                        post("/quotations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(req))
                )
                .andDo((print()))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @MethodSource("generateInvalidQuotationRequests")
    void givenInvalidRequest_whenPostForQuotation_thenReturnBadRequest(QuotationReq quotationReq) throws Exception {
        mockMvc.perform(
                        post("/quotations")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(quotationReq))
                )
                .andDo((print()))
                .andExpect(status().isBadRequest());
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
