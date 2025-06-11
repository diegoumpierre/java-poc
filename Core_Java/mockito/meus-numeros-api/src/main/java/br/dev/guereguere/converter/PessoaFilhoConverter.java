package br.dev.guereguere.converter;

import br.dev.guereguere.dto.request.PessoaFilhoRequestDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponseDTO;
import br.dev.guereguere.entity.PessoaFilho;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;




/**
 *
 * Componente responsável pela transformação de Objetos de domínio para objetos de transferência, bem como operações de
 * validação simples para o objeto PessoaFilho
 *
 * @author Diego Umpierre
 * @since 11/11/2020
 */
@Component
public class PessoaFilhoConverter {

    @Autowired
	PessoaConverter pessoaConverter;

	



  /**
     * Converte o objeto de domínio para o objeto de transferência.
     *
     * @param obj Associado a ser convertido.
     * @return PessoaFilhoResponseDTO ou null caso o parâmetro de entrada seja nulo.
     * @author Diego Umpierre
     */
    public PessoaFilhoResponseDTO toResponseDTO(PessoaFilho obj) {

        if (obj == null) return PessoaFilhoResponseDTO.builder().build();

//		MapperFacade mapper = orikaMapperConfiguration.mapperFacade();
		PessoaFilhoResponseDTO destination = null;//mapper.map(obj,PessoaFilhoResponseDTO.class);
//		destination.setPessoaResponseDTO(pessoaConverter.toResponseDTO(obj.getPessoa()));
		
        return destination;
    }

    /**
     * Converte o objeto de transferência para o objeto de domínio.
     *
     * @param obj AssociadoRequestDTO a ser convertido.
     * @return PessoaFilho ou null caso o parâmetro de entrada seja nulo.
     * @author Diego Umpierre
     */
    public PessoaFilho toDomain(PessoaFilhoRequestDTO obj) {

        if (obj == null) return PessoaFilho.builder().build();

//		MapperFacade mapper = orikaMapperConfiguration.mapperFacade();
		PessoaFilho destination = null;//mapper.map(obj,PessoaFilho.class);
		destination.setPessoa(pessoaConverter.toDomain(obj.getPessoaRequestDTO()));
				
        return destination;


    }

     public PessoaFilhoResponseDTO toResponseDTOFull(PessoaFilho obj) {

        if (obj == null) return PessoaFilhoResponseDTO.builder().build();

//        MapperFacade mapper = orikaMapperConfiguration.mapperFacade();
		PessoaFilhoResponseDTO destination = null;//mapper.map(obj,PessoaFilhoResponseDTO.class);
//        destination.setPessoaResponseDTO(pessoaConverter.toResponseDTO(obj.getPessoa()));
		
        return destination;

    }

}