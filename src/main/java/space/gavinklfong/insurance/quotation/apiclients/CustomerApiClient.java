package space.gavinklfong.insurance.quotation.apiclients;

import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CustomerApiClient {
	Optional<Customer> getCustomerById(Long id) throws IOException;
}
