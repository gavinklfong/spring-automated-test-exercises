package space.gavinklfong.insurance.quotation.services;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import space.gavinklfong.insurance.quotation.apiclients.CustomerSrvClient;
import space.gavinklfong.insurance.quotation.apiclients.ProductSrvClient;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;
import space.gavinklfong.insurance.quotation.apiclients.dto.Product;
import space.gavinklfong.insurance.quotation.dto.QuotationReq;
import space.gavinklfong.insurance.quotation.exceptions.RecordNotFoundException;
import space.gavinklfong.insurance.quotation.models.Quotation;
import space.gavinklfong.insurance.quotation.repositories.QuotationRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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
    private CustomerSrvClient customerSrvClient;

    @MockBean
    private ProductSrvClient productSrvClient;

    @Autowired
    private QuotationService quotationService;

    private final Faker faker = new Faker();

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
    @MethodSource("generateQuotationArguments")
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
        assertThat(result.getProductCode()).isEqualTo(PRODUCT_CODE);

        verify(customerSrvClient, times(1)).getCustomers(req.getCustomerId());
        verify(productSrvClient, times(1)).getProducts(req.getProductCode());
        verify(quotationRepo, times(1)).save(result.withQuotationCode(null));
    }

    static Stream<Arguments> generateQuotationArguments() {
        return Stream.of(
                Arguments.of("ABC", 69, 1000D),
                Arguments.of("ABC", 70, 1500D),
                Arguments.of("SW20", 69, 700D),
                Arguments.of("SW20", 70, 1050D)
        );
    }

    private void givenCondition(int customerAge) throws IOException {
        when(quotationRepo.save(any(Quotation.class))).thenAnswer(invocation -> {
            Quotation quotation = invocation.getArgument(0);
            return quotation.withQuotationCode(UUID.randomUUID().toString());
        });

        List<Customer> customers = asList(
                Customer.builder()
                        .id(CUSTOMER_ID)
                        .dob(LocalDate.now().minusYears(customerAge))
                        .name(faker.name().name())
                        .build()
        );
        when(customerSrvClient.getCustomers(anyLong())).thenReturn(customers);

        when(productSrvClient.getProducts(anyString())).thenReturn(PRODUCT_LIST);
    }

    interface TestConstants {
        String PRODUCT_CODE = "HOME-001";
        long CUSTOMER_ID = 1L;
        long LISTED_PRICE = 1000L;

        int HIGH_RISK_AGE = 70;
        double HIGH_RISK_AGE_ADJ_RATE = 1.5;
        double POST_CODE_DISCOUNT_RATE = 0.3;
        String PRODUCT_POST_CODE = "SW20";

        Product PRODUCT = Product.builder()
                .productCode(PRODUCT_CODE)
                .buildingSumInsured(RandomUtils.nextLong(10000, 50000))
                .contentSumInsured(RandomUtils.nextLong(500, 1000))
                .customerAgeThreshold(HIGH_RISK_AGE)
                .customerAgeThresholdAdjustmentRate(HIGH_RISK_AGE_ADJ_RATE)
                .discountPostCode(new String[]{PRODUCT_POST_CODE})
                .postCodeDiscountRate(POST_CODE_DISCOUNT_RATE)
                .listedPrice(LISTED_PRICE)
                .build();

        List<Product> PRODUCT_LIST = Collections.singletonList(PRODUCT);

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
