package space.gavinklfong.insurance.quotation.repositories;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import space.gavinklfong.insurance.quotation.models.Quotation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class QuotationRepositoryTests {

    private static final String DEFAULT_PRODUCT_CODE = "HOME-001";

    private static final List<Quotation> DEFAULT_QUOTATION_LIST = asList(
            Quotation.builder().quotationCode("862ef3e0-0829-4ba1-b895-05f887b40da8").productCode(DEFAULT_PRODUCT_CODE)
                    .customerId(1L).expiryTime(LocalDateTime.parse("2022-02-01T12:30:00")).amount(1500D).build(),
            Quotation.builder().quotationCode("e6766e0c-df53-4ea8-bede-dbe3b4006798").productCode(DEFAULT_PRODUCT_CODE)
                    .customerId(1L).expiryTime(LocalDateTime.parse("2022-02-14T16:45:00")).amount(2400D).build(),
            Quotation.builder().quotationCode("020b3984-a4d6-4400-8e4d-659787d21f22").productCode(DEFAULT_PRODUCT_CODE)
                    .customerId(2L).expiryTime(LocalDateTime.parse("2022-03-20T01:00:00")).amount(2000D).build()
    );

    @Autowired
    private QuotationRepository quotationRepository;

    private Faker faker = new Faker();

    @AfterEach
    void cleanUp() {
        this.quotationRepository.deleteAll();
    }

    @Test
    @Sql("/quotation-init-data.sql")
    void testFindByQuotationCode() {
        Optional<Quotation> quotation = quotationRepository.findById("862ef3e0-0829-4ba1-b895-05f887b40da8");
        assertThat(quotation).isPresent();
    }

    @Test
    @Sql("/quotation-init-data.sql")
    void testFindByProductCode() {
        List<Quotation> quotations = quotationRepository.findByProductCode("HOME-001");
        assertThat(quotations)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(DEFAULT_QUOTATION_LIST);
    }

    @Test
    void testSave() {
        List<Quotation> quotations = asList(buildQuotationWithRandomValues(), buildQuotationWithRandomValues(), buildQuotationWithRandomValues());
        quotations.forEach(quotation -> quotationRepository.save(quotation));

        List<Quotation> output = quotationRepository.findAll();

        assertThat(output)
                .hasSize(3)
                .containsExactlyInAnyOrderElementsOf(quotations);
    }

    private Quotation buildQuotationWithRandomValues() {
        return Quotation.builder()
                .quotationCode(UUID.randomUUID().toString())
                .productCode(faker.code().toString())
                .amount(faker.number().randomDouble(2, 1000, 50000))
                .customerId(1L)
                .build();
    }
}
