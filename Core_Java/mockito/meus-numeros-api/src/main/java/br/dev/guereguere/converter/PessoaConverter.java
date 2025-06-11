package br.dev.guereguere.converter;


import br.dev.guereguere.dto.request.PessoaRequestDTO;
import br.dev.guereguere.dto.response.PessoaResponseDTO;
import br.dev.guereguere.entity.Pessoa;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.stream.Collectors;




/**
 *
 * Componente responsável pela transformação de Objetos de domínio para objetos de transferência, bem como operações de
 * validação simples para o objeto Pessoa
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Component
public class PessoaConverter {

    @Autowired
	PessoaFilhoConverter pessoaFilhoConverter;

	



  /**
     * Converte o objeto de domínio para o objeto de transferência.
     *
     * @param obj Associado a ser convertido.
     * @return PessoaResponseDTO ou null caso o parâmetro de entrada seja nulo.
     * @author Diego Umpierre
     */
    public PessoaResponseDTO toResponseDTO(Pessoa obj) {

        if (obj == null) return PessoaResponseDTO.builder().build();

//		MapperFacade mapper = orikaMapperConfiguration.mapperFacade();
//		PessoaResponseDTO destination = mapper.map(obj,PessoaResponseDTO.class);
//
//        return destination;
    return null;
    }

    /**
     * Converte o objeto de transferência para o objeto de domínio.
     *
     * @param obj AssociadoRequestDTO a ser convertido.
     * @return Pessoa ou null caso o parâmetro de entrada seja nulo.
     * @author Diego Umpierre
     */
    public Pessoa toDomain(PessoaRequestDTO obj) {

        if (obj == null) return Pessoa.builder().build();

		 return null;
    }

     public PessoaResponseDTO toResponseDTOFull(Pessoa obj) {

        if (obj == null) return PessoaResponseDTO.builder().build();

      return null;

    }

}