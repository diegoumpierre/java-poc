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
 * Objeto que representa uma PessoaFilho.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PessoaFilhoResponsePesquisaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<PessoaFilhoResponseDTO> result;

    private Integer rowCountResult;

    private LinkedHashMap<String,List<ComboDTO>> mapComboDTO;
}
