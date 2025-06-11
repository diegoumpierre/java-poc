package br.dev.guereguere.dto.validation.validator;

import br.dev.guereguere.dto.request.PessoaRequestDTO;
import br.dev.guereguere.dto.validation.PessoaInsert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * Classe que implementa a validação a do Objeto Pessoa, antes de inserir o mesmo.
 *
 *
 * @author Diego Umpierre
 * @since 23/11/2020
 */
@Validated
public class PessoaInsertValidator implements ConstraintValidator<PessoaInsert, PessoaRequestDTO> {

	@Autowired
	private BaseConstraintValidator baseConstraintValidator;
	
	@Override
	public void initialize(PessoaInsert insert) {
	}

	@Override
	public boolean isValid(PessoaRequestDTO dto, ConstraintValidatorContext context) {

		baseConstraintValidator.cleanAllConstraints();

		if (baseConstraintValidator.isValidBasicValidationGroup(dto)){

			//SE O BASICO DE VALIDACAO PASSOU REALIZA VALIDAÇÕES USANDO A SERVICE ACESSO A BANCO E DEMAIS


			//RN05: PAUTA: O nome da pauta não pode se repetir.
//			if(pautaService.existsByNome(dto.getNome())){
//				baseConstraintValidator.addConstraintToField("nome","pauta.msg.nome.ja.usado");
//			}
		}



		return baseConstraintValidator.isValidMsgContext(context);
	}
}