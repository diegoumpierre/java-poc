package br.dev.guereguere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


/**
 *
 * Objeto que representa uma PessoaFilho.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaFilhoResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long oidPessoaFilho;

	private PessoaResponseDTO pessoaResponseDTO;

	private String nomNome;

	
}
