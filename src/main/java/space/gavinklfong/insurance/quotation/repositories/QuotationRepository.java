package space.gavinklfong.insurance.quotation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import space.gavinklfong.insurance.quotation.models.Quotation;

import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, String> {

    List<Quotation> findByProductCode(String productCode);

    List<Quotation> findByCustomerId(Long customerID);

}
