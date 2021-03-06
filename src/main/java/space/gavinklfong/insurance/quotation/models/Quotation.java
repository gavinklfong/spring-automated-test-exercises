package space.gavinklfong.insurance.quotation.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
@Entity
@Table(name = "quotations")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Quotation {
	
	@Id
	private String quotationCode;

	private Double amount;

	@JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private LocalDateTime expiryTime;
	
	private String productCode;

	private Long customerId;
}
