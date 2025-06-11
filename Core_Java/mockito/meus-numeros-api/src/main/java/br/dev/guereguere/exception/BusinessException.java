package br.dev.guereguere.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 3281711407321284640L;

    public BusinessException() {
        super(
                //ResourceBundleUtil.getMessageValue("OBJETO_NAO_PODE_SER_NULO")
        );
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Object... args) {
        super(String.format(message,args));
    }

    public BusinessException(String message, Class clazz) {
        super(clazz.getSimpleName().concat(":").concat(message));
    }

    public BusinessException(String message, String fieldName) {
        super(fieldName.concat(":").concat(message));
    }

}
