package br.dev.guereguere.dto.request;

import br.dev.guereguere.dto.validation.PessoaInsert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import javax.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import br.dev.guereguere.dto.validation.constraints.BasicValidationGroup;
import java.util.List;



/**
 *
 * Objeto a ser usado em uma Requisição para um Pessoa.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@PessoaInsert
public class PessoaRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long oidPessoa;

	@NotEmpty(message="msg.obrigatorio", groups = {BasicValidationGroup.class}) 
	@Length(min=5, max=50, message="pessoa.nomNome", groups = {BasicValidationGroup.class})
	private String nomNome;

	
}
