package br.dev.guereguere.service.impl;

import br.dev.guereguere.converter.ComboConverter;
import br.dev.guereguere.converter.PessoaConverter;
import br.dev.guereguere.dto.ComboDTO;
import br.dev.guereguere.dto.Filter;
import br.dev.guereguere.dto.PageRequestUtils;
import br.dev.guereguere.dto.PesquisaDTO;
import br.dev.guereguere.dto.request.PessoaRequestDTO;
import br.dev.guereguere.dto.response.PessoaResponseDTO;
import br.dev.guereguere.dto.response.PessoaResponsePesquisaDTO;
import br.dev.guereguere.dto.request.PessoaRequestPesquisaDTO;
import br.dev.guereguere.entity.Pessoa;
import br.dev.guereguere.exception.BusinessException;
import br.dev.guereguere.exception.NotFoundException;
import br.dev.guereguere.repository.PessoaRepository;
import br.dev.guereguere.repository.SecurityUserRepository;
import br.dev.guereguere.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import java.util.List;


@Service
public class PessoaServiceImpl implements PessoaService{

	@Autowired
	PageRequestUtils pageRequestUtils;

	@Autowired
	PessoaRepository pessoaRepository;

    @Autowired
    ComboConverter comboConverter;

	@Autowired
	PessoaConverter pessoaConverter;

	

	@Override
	public PessoaResponseDTO save(PessoaRequestDTO pessoaRequestDTO) throws BusinessException {
		Pessoa pessoa = pessoaConverter.toDomain(pessoaRequestDTO);
		pessoa = pessoaRepository.save(pessoa);
		return pessoaConverter.toResponseDTO(pessoa);
	}

	@Override
	public PessoaResponseDTO update(PessoaRequestDTO pessoaRequestDTO) {
		Pessoa pessoa = pessoaConverter.toDomain(pessoaRequestDTO);
		pessoa = pessoaRepository.save(pessoa);
		return pessoaConverter.toResponseDTO(pessoa);
	}

	@Autowired
	SecurityUserRepository securityUserRepository;

	@Override
	public List<PessoaResponseDTO>  findAll() {
		List<Pessoa> pessoaList = StreamSupport.stream(
				pessoaRepository.findAll().spliterator(), true).collect(Collectors.toList());


		 securityUserRepository.findAll();



		return pessoaList.stream().map(entity ->
						pessoaConverter.toResponseDTOFull(entity)).collect(Collectors.toList());
	}

	@Override
	public PessoaResponseDTO findByOid(Long oidPessoa) {
        Pessoa pessoa = pessoaRepository.findById(oidPessoa)
				.orElseThrow(() -> new NotFoundException(Pessoa.class));

        if (pessoa == null) pessoa = new Pessoa();

        return pessoaConverter.toResponseDTOFull(pessoa);
	}

	@Override
	public void deleteByOid(Long oidPessoa) {
		pessoaRepository.deleteByOid(oidPessoa);
	}

	@Override
	public void deleteAllByOid(List<Long> oidPessoaLst) {
		pessoaRepository.deleteAllByOid(oidPessoaLst);
	}

	@Override
	public PessoaResponsePesquisaDTO  search(PessoaRequestPesquisaDTO pessoaRequestPesquisaDTO) {
		PageRequest pageRequest = pageRequestUtils.getPageRequestDTO(pessoaRequestPesquisaDTO.getPesquisaDTO());

		LinkedHashMap<String,List<ComboDTO>> mapComboDTO = new LinkedHashMap<>();
		int rowCount = 0;

		Pessoa filtro = getFilters(pessoaRequestPesquisaDTO.getPesquisaDTO());


		System.out.println(filtro.toString());



		Page<Pessoa> page = pessoaRepository.search(
				pageRequest
				,filtro.getOidPessoa()
				,filtro.getNomNome()
		);
		List<Pessoa> resultado = page.getContent();
		List<PessoaResponseDTO> pessoaResponseDTOS = resultado.stream().map(obj -> pessoaConverter.toResponseDTOFull(obj)).collect(Collectors.toList());


		rowCount = (int) page.getTotalElements();

		System.out.println(pageRequest.toString());

//
//		Page<Pessoa> page = pessoaRepository.search(
//				pageRequest
//				,filtro.getOidPessoa()
//				,filtro.getNomNome()
//		);
//
//		//,filtro.getDtaNascimento() != null ? Date.from(filtro.getDtaNascimento().toInstant()) : null
//        //,filtro.getDtaNascimentoEnd() != null ? Date.from(filtro.getDtaNascimentoEnd().toInstant()) : null
//
//		LinkedHashMap<String,List<ComboDTO>> mapComboDTO = new LinkedHashMap<>();
//		List<Pessoa> resultado = page.getContent();
//		List<PessoaResponseDTO> pessoaResponseDTOS = resultado.stream().map(obj -> pessoaConverter.toResponseDTOFull(obj)).collect(Collectors.toList());
//
//
//
		return PessoaResponsePesquisaDTO.builder()
				.result(pessoaResponseDTOS)
				.rowCountResult(rowCount)
				.mapComboDTO(mapComboDTO)
				.build();
	}

	private Pessoa getFilters(PesquisaDTO pesquisaDTO) {

		//System.out.println(pessoaRequestPesquisaDTO.getPesquisaDTO().getFilters());

		HashMap<String, Filter> filterHashMap = pesquisaDTO.getFilters();

		Pessoa filtro = new Pessoa();

		Filter global = filterHashMap.get("global");
		String globalValue = global.getValue();

		if (globalValue != null){
			try{
				filtro.setOidPessoa(Long.valueOf(globalValue));
			}catch (java.lang.NumberFormatException e){
				System.out.println("warning: Not a number: "+globalValue);
			}

			filtro.setNomNome(globalValue);
		}

		return filtro;

	}


}