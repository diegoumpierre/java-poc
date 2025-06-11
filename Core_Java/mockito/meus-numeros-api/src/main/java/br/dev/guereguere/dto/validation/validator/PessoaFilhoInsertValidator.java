package br.dev.guereguere.dto.validation.validator;

import br.dev.guereguere.dto.request.PessoaFilhoRequestDTO;
import br.dev.guereguere.dto.validation.PessoaFilhoInsert;
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
public class PessoaFilhoInsertValidator implements ConstraintValidator<PessoaFilhoInsert, PessoaFilhoRequestDTO> {

	@Autowired
	private BaseConstraintValidator baseConstraintValidator;
	
	@Override
	public void initialize(PessoaFilhoInsert insert) {
	}

	@Override
	public boolean isValid(PessoaFilhoRequestDTO dto, ConstraintValidatorContext context) {

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