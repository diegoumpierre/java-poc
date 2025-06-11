package br.dev.guereguere.controller;

import br.dev.guereguere.dto.request.PessoaRequestDTO;
import br.dev.guereguere.dto.request.PessoaRequestPesquisaDTO;
import br.dev.guereguere.dto.response.PessoaResponseDTO;
import br.dev.guereguere.dto.response.PessoaResponsePesquisaDTO;
import br.dev.guereguere.service.PessoaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;


@Api(tags = "serv001modelo - Pessoa")
@RestController
@RequestMapping(value="/pessoa")
@Slf4j
public class PessoaController {

    @Autowired
    PessoaService pessoaService;

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Insere Pessoa")
    public  ResponseEntity<PessoaResponseDTO> save(@RequestBody @Valid PessoaRequestDTO pessoaRequestDTO) {
        return ResponseEntity.ok(pessoaService.save(pessoaRequestDTO));
    }

    @RequestMapping(method=RequestMethod.PUT)
    @ApiOperation(value = "Atualiza Pessoa")
    public ResponseEntity<PessoaResponseDTO> update(@RequestBody @Valid PessoaRequestDTO pessoaRequestDTO) {
        return ResponseEntity.ok(pessoaService.update(pessoaRequestDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Lista todas as Pessoas")
    public ResponseEntity<List<PessoaResponseDTO>> findAll() {
        return ResponseEntity.ok(pessoaService.findAll());
    }

    @RequestMapping(value="/{oidPessoa}", method=RequestMethod.GET)
    @ApiOperation(value = "Lista Pessoa por oidPessoa")
    public ResponseEntity<PessoaResponseDTO> findByOid(@PathVariable Long oidPessoa){
        return  ResponseEntity.ok(pessoaService.findByOid(oidPessoa));
    }

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value="/{oidPessoa}", method=RequestMethod.DELETE)
    @ApiOperation(value = "Delete Pessoa por oidPessoa")
    public ResponseEntity<Void> delete(@PathVariable Long oidPessoa) {
        pessoaService.deleteByOid(oidPessoa);
        return ResponseEntity.ok().build();
    }

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value="/{oidPessoaLst}", method=RequestMethod.POST)
    @ApiOperation(value = "Delete Pessoas por oidPessoaLst")
    public ResponseEntity<Void> deleteAllByOid(@PathVariable List<Long> oidPessoaLst) {
        pessoaService.deleteAllByOid(oidPessoaLst);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/page", method=RequestMethod.POST)
    @ApiOperation(value = "Busca todas Pessoas conforme filtros")
    public ResponseEntity<PessoaResponsePesquisaDTO> search(@RequestBody PessoaRequestPesquisaDTO pessoaRequestPesquisaDTO) {
        return ResponseEntity.ok(pessoaService.search(pessoaRequestPesquisaDTO));
    }
}