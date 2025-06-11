package br.dev.guereguere.controller;

import br.dev.guereguere.dto.request.PessoaFilhoRequestDTO;
import br.dev.guereguere.dto.request.PessoaFilhoRequestPesquisaDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponseDTO;
import br.dev.guereguere.dto.response.PessoaFilhoResponsePesquisaDTO;
import br.dev.guereguere.service.PessoaFilhoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import java.util.List;


@Api(tags = "serv001modelo - PessoaFilho")
@RestController
@RequestMapping(value="/pessoaFilho")
@Slf4j
public class PessoaFilhoController {

    @Autowired
    PessoaFilhoService pessoaFilhoService;

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Insere PessoaFilho")
    public  ResponseEntity<PessoaFilhoResponseDTO> save(@RequestBody @Valid PessoaFilhoRequestDTO pessoaFilhoRequestDTO) {
        return ResponseEntity.ok(pessoaFilhoService.save(pessoaFilhoRequestDTO));
    }

    @RequestMapping(method=RequestMethod.PUT)
    @ApiOperation(value = "Atualiza PessoaFilho")
    public ResponseEntity<PessoaFilhoResponseDTO> update(@RequestBody @Valid PessoaFilhoRequestDTO pessoaFilhoRequestDTO) {
        return ResponseEntity.ok(pessoaFilhoService.update(pessoaFilhoRequestDTO));
    }

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "Lista todas as PessoaFilhos")
    public ResponseEntity<List<PessoaFilhoResponseDTO>> findAll() {
        return ResponseEntity.ok(pessoaFilhoService.findAll());
    }

    @RequestMapping(value="/{oidPessoaFilho}", method=RequestMethod.GET)
    @ApiOperation(value = "Lista PessoaFilho por oidPessoaFilho")
    public ResponseEntity<PessoaFilhoResponseDTO> findByOid(@PathVariable Long oidPessoaFilho){
        return  ResponseEntity.ok(pessoaFilhoService.findByOid(oidPessoaFilho));
    }

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value="/{oidPessoaFilho}", method=RequestMethod.DELETE)
    @ApiOperation(value = "Delete PessoaFilho por oidPessoaFilho")
    public ResponseEntity<Void> delete(@PathVariable Long oidPessoaFilho) {
        pessoaFilhoService.deleteByOid(oidPessoaFilho);
        return ResponseEntity.ok().build();
    }

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @RequestMapping(value="/{oidPessoaFilhoLst}", method=RequestMethod.POST)
    @ApiOperation(value = "Delete PessoaFilhos por oidPessoaFilhoLst")
    public ResponseEntity<Void> deleteAllByOid(@PathVariable List<Long> oidPessoaFilhoLst) {
        pessoaFilhoService.deleteAllByOid(oidPessoaFilhoLst);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/page", method=RequestMethod.POST)
    @ApiOperation(value = "Busca todas PessoaFilhos conforme filtros")
    public ResponseEntity<PessoaFilhoResponsePesquisaDTO> search(@RequestBody PessoaFilhoRequestPesquisaDTO pessoaFilhoRequestPesquisaDTO) {
        return ResponseEntity.ok(pessoaFilhoService.search(pessoaFilhoRequestPesquisaDTO));
    }
}