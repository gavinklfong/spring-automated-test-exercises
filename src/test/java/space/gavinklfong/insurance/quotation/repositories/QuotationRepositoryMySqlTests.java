package space.gavinklfong.insurance.quotation.repositories;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import space.gavinklfong.insurance.quotation.models.Quotation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
public class QuotationRepositoryMySqlTests {

    @Container
    static MySQLContainer mySQLDBContainer = new MySQLContainer("mysql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLDBContainer::getJdbcUrl);
    }

    private static final String DEFAULT_PRODUCT_CODE = "HOME-001";

    private static final String QUOTATION_KEY_1 = "862ef3e0-0829-4ba1-b895-05f887b40da8";
    private static final String QUOTATION_KEY_2 = "e6766e0c-df53-4ea8-bede-dbe3b4006798";
    private static final String QUOTATION_KEY_3 = "020b3984-a4d6-4400-8e4d-659787d21f22";

    private static final Map<String, Quotation> DEFAULT_QUOTATION_MAP = Map.of(
            QUOTATION_KEY_1,
            Quotation.builder().quotationCode(QUOTATION_KEY_1).productCode(DEFAULT_PRODUCT_CODE)
                    .customerId(1L).expiryTime(LocalDateTime.parse("2022-02-01T12:30:00")).amount(1500D).build(),
            QUOTATION_KEY_2,
            Quotation.builder().quotationCode(QUOTATION_KEY_2).productCode(DEFAULT_PRODUCT_CODE)
                    .customerId(1L).expiryTime(LocalDateTime.parse("2022-02-14T16:45:00")).amount(2400D).build(),
            QUOTATION_KEY_3,
            Quotation.builder().quotationCode(QUOTATION_KEY_3).productCode(DEFAULT_PRODUCT_CODE)
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

    }

    @Test
    @Sql("/quotation-init-data.sql")
    void testFindByProductCode() {

    }

    @Test
    void testSave() {

    }

}
