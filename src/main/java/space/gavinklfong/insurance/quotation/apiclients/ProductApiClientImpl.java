package space.gavinklfong.insurance.quotation.apiclients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import space.gavinklfong.insurance.quotation.apiclients.dto.Product;

import java.util.Optional;

@Component
public class ProductApiClientImpl implements ProductApiClient {

    private String productSrvUrl;

    public ProductApiClientImpl(@Value("${app.productSrvUrl}") String url) {
        productSrvUrl = url;
    }

    @Override
    public Optional<Product> getProductByCode(String code) {
        WebClient webClient = WebClient.create(productSrvUrl);
        Product product = webClient.get()
                .uri("/products/" + code)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->  ( Mono.empty() ))
                .bodyToMono(Product.class)
                .block();

        return Optional.ofNullable(product);
    }
}
