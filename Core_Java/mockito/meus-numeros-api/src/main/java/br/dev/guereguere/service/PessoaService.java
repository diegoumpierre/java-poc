package br.dev.guereguere.service;

import br.dev.guereguere.dto.request.PessoaRequestDTO;
import br.dev.guereguere.dto.request.PessoaRequestPesquisaDTO;
import br.dev.guereguere.dto.response.PessoaResponseDTO;
import br.dev.guereguere.dto.response.PessoaResponsePesquisaDTO;
import br.dev.guereguere.entity.Pessoa;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public interface PessoaService{

    PessoaResponseDTO save(PessoaRequestDTO pessoaRequestDTO);
    PessoaResponseDTO update(PessoaRequestDTO pessoaRequestDTO);
    List<PessoaResponseDTO> findAll();
    PessoaResponseDTO findByOid(Long oidPessoa);
    void deleteByOid(Long oidPessoa);
	void deleteAllByOid(List<Long> oidPessoaLst);
    PessoaResponsePesquisaDTO search(PessoaRequestPesquisaDTO pessoaRequestPesquisaDTO);
    
}