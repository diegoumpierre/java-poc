package br.dev.guereguere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import java.util.List;


/**
 *
 * Objeto que representa uma Pessoa.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long oidPessoa;
	private String nomNome;

	
}
