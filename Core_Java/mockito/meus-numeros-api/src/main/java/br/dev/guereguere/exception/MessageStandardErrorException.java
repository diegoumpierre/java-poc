package br.dev.guereguere.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * Classe para o tratamento de excessão personalizado
 *
 * @author Diego Umpierre
 * @since 16/11/2020
 *
 */
@Data
public class MessageStandardErrorException implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long timestamp;
	private Integer status;
	private String error;
	private String message;
	private String path;
	
	public MessageStandardErrorException(Long timestamp, Integer status, String error, String message, String path) {
		super();
		this.timestamp = timestamp;
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}
	public MessageStandardErrorException() {
		super();
	}

}
