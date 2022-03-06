package space.gavinklfong.insurance.quotation.apiclients;

import space.gavinklfong.insurance.quotation.apiclients.dto.Product;

import java.util.List;

public interface ProductSrvClient {
	List<Product> getProducts(String id);
	
}
