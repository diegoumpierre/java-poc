package br.dev.guereguere.exception;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe personalizada para tratamento de excessão
 *
 * @author Diego Umpierre
 * @since 16/11/2020
 *
 */
public class MessageValidationErrorException extends MessageStandardErrorException {

	private static final long serialVersionUID = 1L;

	private List<MessageFieldException> errors = new ArrayList<>();

	public MessageValidationErrorException(Long timestamp, Integer status, String error, String message, String path) {
		super(timestamp, status, error, message, path);
	}

	public List<MessageFieldException> getErrors() {
		return errors;
	}

	public void addError(String fieldName, String messagem) {
		errors.add(new MessageFieldException(fieldName, messagem));
	}
}
