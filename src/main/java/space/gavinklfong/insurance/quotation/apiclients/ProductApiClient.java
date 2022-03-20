package space.gavinklfong.insurance.quotation.apiclients;

import space.gavinklfong.insurance.quotation.apiclients.dto.Product;

import java.util.List;
import java.util.Optional;

public interface ProductApiClient {
	Optional<Product> getProductByCode(String code);
}
