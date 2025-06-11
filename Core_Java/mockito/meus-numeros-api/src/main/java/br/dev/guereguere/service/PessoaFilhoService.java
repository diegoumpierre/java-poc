package br.dev.guereguere.service;

import br.dev.guereguere.dto.request.PessoaFilhoRequestDTO;
import br.dev.guereguere.dto.request.PessoaFilhoRequestPesquisaDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponseDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponsePesquisaDTO;
import br.dev.guereguere.entity.PessoaFilho;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public interface PessoaFilhoService{

    PessoaFilhoResponseDTO save(PessoaFilhoRequestDTO pessoaFilhoRequestDTO);
    PessoaFilhoResponseDTO update(PessoaFilhoRequestDTO pessoaFilhoRequestDTO);
    List<PessoaFilhoResponseDTO> findAll();
    PessoaFilhoResponseDTO findByOid(Long oidPessoaFilho);
    void deleteByOid(Long oidPessoaFilho);
	void deleteAllByOid(List<Long> oidPessoaFilhoLst);
    PessoaFilhoResponsePesquisaDTO search(PessoaFilhoRequestPesquisaDTO pessoaFilhoRequestPesquisaDTO);
    List<PessoaFilho> findByOidPessoa(Long oidPessoa);

}