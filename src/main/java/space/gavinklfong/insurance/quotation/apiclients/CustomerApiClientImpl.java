package space.gavinklfong.insurance.quotation.apiclients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;

import java.io.IOException;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class CustomerApiClientImpl implements CustomerApiClient {

    private String customerSrvUrl;

    public CustomerApiClientImpl(@Value("${app.customerSrvUrl}") String url) {
        customerSrvUrl = url;
    }

    @Override
    public Optional<Customer> getCustomerById(Long id) throws IOException {

        WebClient webClient = WebClient.create(customerSrvUrl);
        Customer customer = webClient.get()
                .uri("/customers/" + id)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->  ( Mono.empty() ))
                .bodyToMono(Customer.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(3)))
                .block();

        return Optional.ofNullable(customer);
    }

    public Customer saveCustomer(Customer customer) {

        WebClient webClient = WebClient.create(customerSrvUrl);
        Mono<Customer> savedCustomer = webClient.post()
                .uri("/customer")
                .body(Mono.just(customer), Customer.class)
                .retrieve()
                .bodyToMono(Customer.class);

        return savedCustomer.block();
    }

}
