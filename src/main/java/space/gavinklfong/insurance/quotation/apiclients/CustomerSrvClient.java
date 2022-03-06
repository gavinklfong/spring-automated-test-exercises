package space.gavinklfong.insurance.quotation.apiclients;

import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;

import java.io.IOException;
import java.util.List;

public interface CustomerSrvClient {

	List<Customer> getCustomers(Long id) throws IOException;
	
}
