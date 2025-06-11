package br.dev.guereguere.dto.request;

import br.dev.guereguere.dto.PesquisaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;



/**
 *
 * Objeto a ser usado em uma Requisição para um PessoaFilho.
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PessoaFilhoRequestPesquisaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private PesquisaDTO pesquisaDTO;

    private PessoaFilhoRequestDTO filtroPessoaFilhoRequestDTO;
}
