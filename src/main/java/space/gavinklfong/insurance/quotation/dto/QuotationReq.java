package space.gavinklfong.insurance.quotation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotationReq {

	@NotNull
	@NotBlank
	private String productCode;
	@NotNull
	private Long customerId;
	private String postCode;
}
