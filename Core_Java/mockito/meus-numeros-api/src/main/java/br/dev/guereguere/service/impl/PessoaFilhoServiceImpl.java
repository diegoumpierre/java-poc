package br.dev.guereguere.service.impl;

import br.dev.guereguere.converter.ComboConverter;
import br.dev.guereguere.converter.PessoaFilhoConverter;
import br.dev.guereguere.dto.ComboDTO;
import br.dev.guereguere.dto.PageRequestUtils;
import br.dev.guereguere.dto.request.PessoaFilhoRequestDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponseDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponsePesquisaDTO;
import br.dev.guereguere.dto.request.PessoaFilhoRequestPesquisaDTO;
import br.dev.guereguere.entity.PessoaFilho;
import br.dev.guereguere.exception.BusinessException;
import br.dev.guereguere.exception.NotFoundException;
import br.dev.guereguere.repository.PessoaFilhoRepository;
import br.dev.guereguere.service.PessoaFilhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import br.dev.guereguere.service.PessoaService;



@Service
public class PessoaFilhoServiceImpl implements PessoaFilhoService{

	@Autowired
	PageRequestUtils pageRequestUtils;

	@Autowired
	PessoaFilhoRepository pessoaFilhoRepository;

    @Autowired
    ComboConverter comboConverter;

	@Autowired
	PessoaFilhoConverter pessoaFilhoConverter;

	@Autowired
	PessoaService pessoaService;

	@Override
	public PessoaFilhoResponseDTO save(PessoaFilhoRequestDTO pessoaFilhoRequestDTO) throws BusinessException {
		PessoaFilho pessoaFilho = pessoaFilhoConverter.toDomain(pessoaFilhoRequestDTO);
		pessoaFilho = pessoaFilhoRepository.save(pessoaFilho);
		return pessoaFilhoConverter.toResponseDTO(pessoaFilho);
	}

	@Override
	public PessoaFilhoResponseDTO update(PessoaFilhoRequestDTO pessoaFilhoRequestDTO) {
		PessoaFilho pessoaFilho = pessoaFilhoConverter.toDomain(pessoaFilhoRequestDTO);
		pessoaFilho = pessoaFilhoRepository.save(pessoaFilho);
		return pessoaFilhoConverter.toResponseDTO(pessoaFilho);
	}

	@Override
	public List<PessoaFilhoResponseDTO>  findAll() {
		List<PessoaFilho> pessoaList = StreamSupport.stream(
				pessoaFilhoRepository.findAll().spliterator(), true).collect(Collectors.toList());

		return pessoaList.stream().map(entity ->
						pessoaFilhoConverter.toResponseDTOFull(entity)).collect(Collectors.toList());
	}

	@Override
	public PessoaFilhoResponseDTO findByOid(Long oidPessoaFilho) {
        PessoaFilho pessoaFilho = pessoaFilhoRepository.findById(oidPessoaFilho)
				.orElseThrow(() -> new NotFoundException(PessoaFilho.class));

        if (pessoaFilho == null) pessoaFilho = new PessoaFilho();

        return pessoaFilhoConverter.toResponseDTOFull(pessoaFilho);
	}

	@Override
	public void deleteByOid(Long oidPessoaFilho) {
		pessoaFilhoRepository.deleteByOid(oidPessoaFilho);
	}

	@Override
	public void deleteAllByOid(List<Long> oidPessoaFilhoLst) {
		pessoaFilhoRepository.deleteAllByOid(oidPessoaFilhoLst);
	}

	@Override
	public PessoaFilhoResponsePesquisaDTO  search(PessoaFilhoRequestPesquisaDTO pessoaFilhoRequestPesquisaDTO) {
		PageRequest pageRequest = pageRequestUtils.getPageRequestDTO(pessoaFilhoRequestPesquisaDTO.getPesquisaDTO());
		PessoaFilho filtro = pessoaFilhoConverter.toDomain(pessoaFilhoRequestPesquisaDTO.getFiltroPessoaFilhoRequestDTO());
		Page<PessoaFilho> page = pessoaFilhoRepository.search(
				pageRequest 
				,filtro.getOidPessoaFilho()
				,filtro.getPessoa() != null && filtro.getPessoa().getOidPessoa() != null  ? filtro.getPessoa().getOidPessoa() : null 
				,filtro.getPessoa() != null && filtro.getPessoa().getNomNome() != null  ? filtro.getPessoa().getNomNome() : null 
				,filtro.getNomNome()
		);

		//,filtro.getDtaNascimento() != null ? Date.from(filtro.getDtaNascimento().toInstant()) : null
        //,filtro.getDtaNascimentoEnd() != null ? Date.from(filtro.getDtaNascimentoEnd().toInstant()) : null

		LinkedHashMap<String,List<ComboDTO>> mapComboDTO = new LinkedHashMap<>();
		List<PessoaFilho> resultado = page.getContent();
		List<PessoaFilhoResponseDTO> pessoaFilhoResponseDTOS = resultado.stream().map(obj -> pessoaFilhoConverter.toResponseDTOFull(obj)).collect(Collectors.toList());

        List<ComboDTO> comboPessoa = new ArrayList<>();
		pessoaService.findAll().stream().forEach(
		pessoaResponseDTO -> comboPessoa.add(comboConverter.toComboDTO(
			pessoaResponseDTO.getOidPessoa(),
			pessoaResponseDTO.getNomNome())
		));
		Collections.sort(comboPessoa);
		mapComboDTO.put("comboPessoa",comboPessoa);

		return PessoaFilhoResponsePesquisaDTO.builder()
				.result(pessoaFilhoResponseDTOS)
				.rowCountResult((int) (long) page.getTotalElements())
				.mapComboDTO(mapComboDTO)
				.build();
	}

    @Override
	public List<PessoaFilho> findByOidPessoa(Long oidPessoa){
		List<PessoaFilho> pessoaList = StreamSupport.stream(
			pessoaFilhoRepository.findAllByOidPessoa(oidPessoa).spliterator(), true).collect(Collectors.toList());
		return pessoaList;
	}

}