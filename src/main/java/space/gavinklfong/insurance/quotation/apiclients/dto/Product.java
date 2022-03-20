package space.gavinklfong.insurance.quotation.apiclients.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class Product {
	private String id;
	private Long buildingSumInsured;
	private Long contentSumInsured;
	private Integer customerAgeThreshold;
	private Double customerAgeThresholdAdjustmentRate;
	private String[] discountPostCode;
	private Double postCodeDiscountRate;
	private Long listedPrice;
	private Map<String, Object> details;
}
