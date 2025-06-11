package br.dev.guereguere.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * Classe criada para personalizar o retorno com erro.
 * Tratar a response
 *
 * @author Diego Umpierre
 * @since 16/11/2020
 */
@Data
public class MessageFieldException implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fieldName;
	private String message;

	public MessageFieldException() {
	}

	public MessageFieldException(String fieldName, String message) {
		super();
		this.fieldName = fieldName;
		this.message = message;
	}

}
