package br.dev.guereguere.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4246380622545820922L;

    public NotFoundException(Class<?> clazz) {
        //super(clazz.getSimpleName().concat(":").concat(ResourceBundleUtil.getMessageValue("OBJETO_NAO_ENCONTRADO")));
    }

}
