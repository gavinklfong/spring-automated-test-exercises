package space.gavinklfong.insurance.quotation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RecordNotFoundException extends RuntimeException {

	public RecordNotFoundException() {
		super();
	}
	
	public RecordNotFoundException(String message) {
		super(message);
	}
}
