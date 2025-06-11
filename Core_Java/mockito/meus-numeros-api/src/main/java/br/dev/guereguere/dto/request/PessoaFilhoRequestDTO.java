package br.dev.guereguere.dto.request;

import br.dev.guereguere.dto.validation.PessoaFilhoInsert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import br.dev.guereguere.dto.validation.constraints.BasicValidationGroup;



/**
 *
 * Objeto a ser usado em uma Requisição para um PessoaFilho.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@PessoaFilhoInsert
public class PessoaFilhoRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long oidPessoaFilho;

	private PessoaRequestDTO pessoaRequestDTO;

	@NotEmpty(message="msg.obrigatorio", groups = {BasicValidationGroup.class}) 
	@Length(min=5, max=50, message="pessoaFilho.nomNome", groups = {BasicValidationGroup.class})
	private String nomNome;

	
}
