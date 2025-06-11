package br.dev.guereguere.dto.validation;


import br.dev.guereguere.dto.validation.validator.PessoaInsertValidator;
import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Interface para personalizar as constraint
 *
 * @author Diego Umpierre
 * @since 17/11/2020
 */
@Constraint(validatedBy = PessoaInsertValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PessoaInsert {

	String message() default "ERROR_VALIDATION";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
