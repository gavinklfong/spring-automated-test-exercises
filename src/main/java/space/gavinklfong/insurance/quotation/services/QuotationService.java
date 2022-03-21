package space.gavinklfong.insurance.quotation.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import space.gavinklfong.insurance.quotation.apiclients.CustomerApiClient;
import space.gavinklfong.insurance.quotation.apiclients.ProductApiClient;
import space.gavinklfong.insurance.quotation.apiclients.dto.Customer;
import space.gavinklfong.insurance.quotation.apiclients.dto.Product;
import space.gavinklfong.insurance.quotation.dto.QuotationReq;
import space.gavinklfong.insurance.quotation.exceptions.RecordNotFoundException;
import space.gavinklfong.insurance.quotation.models.Quotation;
import space.gavinklfong.insurance.quotation.repositories.QuotationRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
@Service
public class QuotationService {

	@Value("${app.quotation.expiryTime}")
	private Integer quotationExpiryTime;

	@Autowired
	private QuotationRepository quotationRepo;

	@Autowired
	private CustomerApiClient customerSrvClient;
	
	@Autowired
	private ProductApiClient productSrvClient;

	public Quotation generateQuotation(QuotationReq request) throws IOException, RecordNotFoundException {
		
		Double quotationAmount;
		
		// get customer info
		Customer customer = retrieveCustomer(request.getCustomerId());

		// get product spec
		Product product = retrieveProduct(request.getProductCode());

		// check customer age against the threshold defined in product spec
		//
		// If customer's age exceeds the threshold, increase the quote by 50%
		// Otherwise, get the listed price
		//
		LocalDateTime now = LocalDateTime.now();
		Period period = Period.between(customer.getDob(), now.toLocalDate());
		quotationAmount =
				(period.getYears() >= product.getCustomerAgeThreshold())?
				product.getListedPrice() * product.getCustomerAgeThresholdAdjustmentRate():
				product.getListedPrice().doubleValue();

		log.debug("After customer age check, quotation amount = " + quotationAmount);

		// check if post code is in the discount list
		//
		// Offer discount if customer's post code matches the specification in product info
		//
		if (product.getDiscountPostCode() != null) {
			boolean found = Arrays.stream(product.getDiscountPostCode())
							.anyMatch(x -> x.equalsIgnoreCase(request.getPostCode()));
			if (found) {
				log.debug("Postcode matched, apply discount rate = " + product.getPostCodeDiscountRate());
				quotationAmount *= (1 - product.getPostCodeDiscountRate());
			}
		}

		log.debug("After postcode check, amount = " + quotationAmount);
		
		// Construct quotation and save to data store
		Quotation quotation = Quotation.builder()
				.quotationCode(UUID.randomUUID().toString())
				.expiryTime(now.plusMinutes(quotationExpiryTime))
				.customerId(request.getCustomerId())
				.productCode(request.getProductCode())
				.amount(quotationAmount)
				.build();
		
		quotationRepo.save(quotation);

		return quotation;
	}
	
	public Quotation retrieveQuotation(String quotationCode) {
		Optional<Quotation> quotation = quotationRepo.findById(quotationCode);
		if (quotation.isEmpty()) throw new RecordNotFoundException(format("quotation %s does not exist", quotationCode));

		return quotation.get();
	}

	public List<Quotation> retrieveQuotationByCustomerId(Long customerId) {
		if (isNull(customerId)) return Collections.emptyList();
		return quotationRepo.findByCustomerId(customerId);
	}

	private Customer retrieveCustomer(Long id) throws RecordNotFoundException, IOException {
		Optional<Customer> customer = customerSrvClient.getCustomerById(id);
		if (customer.isEmpty()) throw new RecordNotFoundException("Customer record not found");
		return customer.get();
	}
	
	private Product retrieveProduct(String productCode) throws RecordNotFoundException {
		Optional<Product> product = productSrvClient.getProductByCode(productCode);
		if (product.isEmpty()) throw new RecordNotFoundException("Product record not found");
		return product.get();
		
	}
	
	
	
}
