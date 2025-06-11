package br.dev.guereguere.converter;

import br.dev.guereguere.dto.ComboDTO;
import org.springframework.stereotype.Component;

@Component
public class ComboConverter {


    public ComboDTO toComboDTO(Long value, String label) {

        return ComboDTO.builder()
                .value(value)
                .label(label)
                .build();
    }


}
