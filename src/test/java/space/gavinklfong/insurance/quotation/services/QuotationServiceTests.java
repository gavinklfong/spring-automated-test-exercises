package space.gavinklfong.insurance.quotation.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import space.gavinklfong.insurance.quotation.apiclients.CustomerApiClient;
import space.gavinklfong.insurance.quotation.apiclients.ProductApiClient;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;
import space.gavinklfong.insurance.quotation.apiclients.dto.Product;
import space.gavinklfong.insurance.quotation.dto.QuotationReq;
import space.gavinklfong.insurance.quotation.exceptions.RecordNotFoundException;
import space.gavinklfong.insurance.quotation.models.Quotation;
import space.gavinklfong.insurance.quotation.repositories.QuotationRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static space.gavinklfong.insurance.quotation.services.QuotationServiceTests.TestConstants.*;

@Slf4j
@SpringJUnitConfig(QuotationService.class)
@TestPropertySource(properties = {
        "app.quotation.expiryTime=1440"
})
@Tag("UnitTest")
class QuotationServiceTests {

    @MockBean
    private QuotationRepository quotationRepo;

    @MockBean
    private CustomerApiClient customerSrvClient;

    @MockBean
    private ProductApiClient productSrvClient;

    @Autowired
    private QuotationService quotationService;

    @Test
    void givenQuotations_whenRetrieveByCustomerId_thenReturnQuotations() {

        final Long CUSTOMER_ID = 1L;

        Quotation quotation = Quotation.builder()
                .quotationCode(UUID.randomUUID().toString())
                .productCode("HOME-0001")
                .customerId(CUSTOMER_ID)
                .amount(1500D)
                .build();

        when(quotationRepo.findByCustomerId(CUSTOMER_ID)).thenReturn(asList(quotation));

        List<Quotation> output = quotationService.retrieveQuotationByCustomerId(CUSTOMER_ID);

        assertThat(output).hasSize(1).containsExactly(quotation);
    }

    @Test
    void givenQuotationExists_whenRetrieveQuotation_thenReturnQuotation() {

        when(quotationRepo.findById(QUOTATION_CODE)).thenReturn(Optional.of(QUOTATION));

        Quotation result = quotationService.retrieveQuotation(QUOTATION_CODE);

        assertThat(result).isEqualTo(QUOTATION);
        verify(quotationRepo, times(1)).findById(QUOTATION_CODE);
    }

    @Test
    void givenQuotationNotFound_whenRetrieveQuotation_thenThrowRecordNotFoundException() {

        when(quotationRepo.findById(QUOTATION_CODE)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> quotationService.retrieveQuotation(QUOTATION_CODE));

        verify(quotationRepo, times(1)).findById(QUOTATION_CODE);
    }

    @ParameterizedTest
    @MethodSource("generateQuotationTestScenarios")
    void givenPostCodeAndCustomerAge_whenGenerateQuotation_thenReturnQuote(String postCode, int customerAge, double expectedAmount) throws RecordNotFoundException, IOException {

        // Given
        givenCondition(customerAge);

        // When
        QuotationReq req = QuotationReq.builder()
                .customerId(CUSTOMER_ID)
                .postCode(postCode)
                .productCode(PRODUCT_CODE)
                .build();
        Quotation result = quotationService.generateQuotation(req);

        // Then
        assertThat(result.getAmount()).isEqualTo(expectedAmount);
        assertThat(result.getExpiryTime()).isAfter(LocalDateTime.now());
        assertThat(result.getCustomerId()).isEqualTo(CUSTOMER_ID);
        assertThat(result.getProductCode()).isEqualTo(PRODUCT_CODE);

        verify(customerSrvClient, times(1)).getCustomerById(req.getCustomerId());
        verify(productSrvClient, times(1)).getProductByCode(req.getProductCode());
        verify(quotationRepo, times(1)).save(result);
    }

    /**
     * Generate test scenarios for quotation generation
     *
     * @return Stream of arguments - Postcode, Customer age, Expected quotation amount
     */
    static Stream<Arguments> generateQuotationTestScenarios() {
        return Stream.of(
                Arguments.of("ABC", 69, 1000D),
                Arguments.of("ABC", 70, 1500D),
                Arguments.of("SE18", 69, 700D),
                Arguments.of("SE18", 70, 1050D)
        );
    }

    private void givenCondition(int customerAge) throws IOException {
        when(quotationRepo.save(any(Quotation.class)))
                .thenAnswer(invocation -> (invocation.getArgument(0)));

        Customer customer =
                Customer.builder()
                        .id(CUSTOMER_ID)
                        .dob(LocalDate.now().minusYears(customerAge))
                        .name(CUSTOMER_NAME)
                        .build();
        when(customerSrvClient.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        when(productSrvClient.getProductByCode(PRODUCT_CODE)).thenReturn(Optional.of(PRODUCT));
    }

    @Test
    void givenProductNotFound_whenGenerateQuotation_thenThrowException() throws RecordNotFoundException, IOException {

        // Given
        Customer customer =
                Customer.builder()
                        .id(CUSTOMER_ID)
                        .dob(LocalDate.of(2000, 1, 28))
                        .name(CUSTOMER_NAME)
                        .build();
        when(customerSrvClient.getCustomerById(CUSTOMER_ID)).thenReturn(Optional.of(customer));

        when(productSrvClient.getProductByCode(anyString())).thenThrow(new RecordNotFoundException());

        // When
        QuotationReq req = QuotationReq.builder()
                .customerId(CUSTOMER_ID)
                .postCode("ABC")
                .productCode(PRODUCT_CODE)
                .build();
        assertThrows(RecordNotFoundException.class, () -> quotationService.generateQuotation(req));

        // Then
        verify(customerSrvClient, times(1)).getCustomerById(req.getCustomerId());
        verify(productSrvClient, times(1)).getProductByCode(req.getProductCode());
        verifyNoInteractions(quotationRepo);
    }

    @Test
    void givenCustomerNotFound_whenGenerateQuotation_thenThrowException() throws RecordNotFoundException, IOException {

        // Given
        when(customerSrvClient.getCustomerById(anyLong())).thenThrow(new RecordNotFoundException());

        // When
        QuotationReq req = QuotationReq.builder()
                .customerId(CUSTOMER_ID)
                .postCode("ABC")
                .productCode(PRODUCT_CODE)
                .build();
        assertThrows(RecordNotFoundException.class, () -> quotationService.generateQuotation(req));

        // Then
        verify(customerSrvClient, times(1)).getCustomerById(req.getCustomerId());
        verifyNoInteractions(productSrvClient);
        verifyNoInteractions(quotationRepo);
    }

    interface TestConstants {
        String PRODUCT_CODE = "HOME-001";
        long CUSTOMER_ID = 1L;
        String CUSTOMER_NAME = "John Don";
        long LISTED_PRICE = 1000L;

        int HIGH_RISK_AGE = 70;
        double HIGH_RISK_AGE_ADJ_RATE = 1.5;
        double POSTCODE_DISCOUNT_RATE = 0.3;
        String DISCOUNT_POSTCODE = "SE18";

        Product PRODUCT = Product.builder()
                .id(PRODUCT_CODE)
                .buildingSumInsured(RandomUtils.nextLong(10000, 50000))
                .contentSumInsured(RandomUtils.nextLong(500, 1000))
                .customerAgeThreshold(HIGH_RISK_AGE)
                .customerAgeThresholdAdjustmentRate(HIGH_RISK_AGE_ADJ_RATE)
                .discountPostCode(new String[]{DISCOUNT_POSTCODE})
                .postCodeDiscountRate(POSTCODE_DISCOUNT_RATE)
                .listedPrice(LISTED_PRICE)
                .build();

        String QUOTATION_CODE = UUID.randomUUID().toString();
        Double QUOTATION_AMOUNT = 1200D;

        Quotation QUOTATION = Quotation.builder()
                .quotationCode(QUOTATION_CODE)
                .productCode(PRODUCT_CODE)
                .customerId(CUSTOMER_ID)
                .amount(QUOTATION_AMOUNT)
                .expiryTime(LocalDateTime.now().plusDays(1))
                .build();
    }
}
