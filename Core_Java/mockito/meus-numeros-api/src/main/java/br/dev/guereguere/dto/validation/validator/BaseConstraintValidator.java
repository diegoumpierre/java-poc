package br.dev.guereguere.dto.validation.validator;

import br.dev.guereguere.dto.validation.constraints.ExpensiveValidationGroup;
import br.dev.guereguere.dto.validation.constraints.BasicValidationGroup;
import br.dev.guereguere.exception.MessageFieldException;
import org.springframework.stereotype.Component;

import javax.validation.*;
import java.util.*;


/**
 *
 * Como não foi possível trocar a ordem de execução das constraint  por anotação
 * se criou essa classe que implementa as chmadas básicas antes de realizar validações
 * com service por exemplo
 *
 * @author Diego Umpierre
 * @since 19/11/2020
 *
 */
@Component
public class BaseConstraintValidator {


	private Map<String,List<MessageFieldException>> fieldMessageMap = new HashMap<String,List<MessageFieldException>>();

	/**
	 * Método que realiza a chamada do grupo de Constraints BasicValidationGroup
	 * @param objToValidate objeto a ser validado
	 * @return true se tiver passado por todas as constraints false caso alguma falhe
	 *
	 * @author Diego Umpierre
	 * @since 19/11/2020
	 */
	public boolean isValidBasicValidationGroup(Object objToValidate) {
		//1. BasicValidationGroup
		return isValidValidationGroup(objToValidate, BasicValidationGroup.class);
	}

	/**
	 * Método que realiza a chamada do grupo de Constraints ExpensiveValidationGroup
	 * @param objToValidate objeto a ser validado
	 * @return true se tiver passado por todas as constraints false caso alguma falhe
	 *
	 * @author Diego Umpierre
	 * @since 19/11/2020
	 */
	protected boolean isValidExpensiveValidationGroup(Object objToValidate) {
		//2. ExpensiveValidationGroup
		return isValidValidationGroup(objToValidate, ExpensiveValidationGroup.class);
	}


	/**
	 *
	 * Realiza as validações no objeto conforme constaints existentes no mesmo e conforme parâmetro de grupo
	 *
	 * @param objectToValidate que possui as validações
	 * @param groups grupo de validação a ser realizado as validações
	 * @return false se alguma constraint for violada, caso contrário, retorna true
	 *
	 * @author Diego Umpierre
	 * @since 16/11/2020
	 */
	private boolean isValidValidationGroup (Object objectToValidate,Class<?>... groups){

		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		Validator validator = validatorFactory.getValidator();

		Set<ConstraintViolation<Object>> constraintViolations = validator.validate(objectToValidate, groups);

		if(constraintViolations.size() > 0){

			String msgConstraint = null;

			List<MessageFieldException> list = null;

			for(ConstraintViolation constraintViolation :constraintViolations){

				//adicionando a mensagem personalizada
				msgConstraint = ResourceBundleUtil.getMessageValue(constraintViolation.getMessage());

				//caso não exista a mensagem no resourceBundle pega a mensagem passada por parametro
				if (msgConstraint == null) msgConstraint = constraintViolation.getMessage();

				//verifica se já existe o item no mapa
				list = fieldMessageMap.get(objectToValidate.getClass().getName());
				if (list == null) list = new ArrayList<>();
				list.add(new MessageFieldException(constraintViolation.getPropertyPath().toString(), msgConstraint));

				fieldMessageMap.put(objectToValidate.getClass().getName(),list);

			}
		}
		return fieldMessageMap.size() < 1 ;

	}


	/**
	 * Intera o objeto List<FieldMessage> list da classe e adiciona as mensagens de constraints violadas
	 * ao contexto caso existam.
	 *
	 * @param context contexto da aplicação
	 * @return true se list estiver vazia
	 */
	public boolean isValidMsgContext(ConstraintValidatorContext context){

		if (fieldMessageMap.size() > 0){

			for (String key:fieldMessageMap.keySet()) {

				for (MessageFieldException field: fieldMessageMap.get(key) ) {
					context.disableDefaultConstraintViolation();
					context.buildConstraintViolationWithTemplate(field.getMessage()).addPropertyNode(field.getFieldName()).addConstraintViolation();
				}
			}
		}

		return fieldMessageMap.size() < 1;

	}

	/**
	 * Caso tenha sido validado um DTO e quera se setar o nome do fieldName para que se possa dar o retorno correto na
	 * response
	 *
	 * @param classNameToChange Nome da classe dto que será trocada pelo nome do parametro
	 * @param fieldName Nome do campo a qual deve ser atribuido as constraints
	 */
	public void setConstraintsToFieldName(String classNameToChange, String fieldName) {

		List<MessageFieldException> list = fieldMessageMap.get(classNameToChange);

		if (list != null) {
			fieldMessageMap.remove(classNameToChange);

			List<MessageFieldException> newList = new ArrayList<>();
			for (MessageFieldException field: list) {
				newList.add(new MessageFieldException(fieldName,field.getFieldName()+" - "+field.getMessage()));
			}
			fieldMessageMap.put(fieldName,newList);

		}
	}

	/**
	 * Adiciona uma constraint personalizada a um determinado campo
	 *
	 * @param fieldName nome do campo
	 * @param message mensagem personalizada
	 */
	public void addConstraintToField(String fieldName, String message){

		List<MessageFieldException> list = fieldMessageMap.get(fieldName);

		if (list == null) list = new ArrayList<MessageFieldException>();

		list.add(new MessageFieldException(fieldName,ResourceBundleUtil.getMessageValue(message)));

		fieldMessageMap.put(fieldName,list);

	}



	/**
	 * Limpa o mapa de constraint
	 */
	public void cleanAllConstraints(){
		fieldMessageMap.clear();
	}


	/**
	 * Verifica se existe alguma regra que tenha falhado para um determinado campo
	 *
	 * @param fieldName nome do campo
	 * @return true caso existam regras que tenha falhado e false para caso não exista
	 */
	public boolean existConstraintFailForField(String fieldName){

		List<MessageFieldException> lst = fieldMessageMap.get(fieldName);

		if (lst == null) return false;

		return !lst.isEmpty();
	}

}