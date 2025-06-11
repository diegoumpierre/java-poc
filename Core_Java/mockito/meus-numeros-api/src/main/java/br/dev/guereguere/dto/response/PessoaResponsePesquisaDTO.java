package br.dev.guereguere.dto.response;

import br.dev.guereguere.dto.ComboDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.*;


/**
 *
 * Objeto que representa uma Pessoa.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaResponsePesquisaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PessoaResponseDTO> result;

    private Integer rowCountResult;

    private LinkedHashMap<String,List<ComboDTO>> mapComboDTO;
}
