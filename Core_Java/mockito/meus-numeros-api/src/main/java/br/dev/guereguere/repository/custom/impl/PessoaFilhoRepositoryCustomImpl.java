package br.dev.guereguere.repository.custom.impl;

//import br.dev.guereguere.dto.response.PessoaFilhoResponseDTO;
//import br.dev.guereguere.dto.response.PessoaResponseDTO;
//import br.dev.guereguere.entity.Pessoa;
//import br.dev.guereguere.entity.PessoaFilho;
//import br.dev.guereguere.repository.custom.PessoaFilhoRepositoryCustom;
import br.dev.guereguere.repository.custom.PessoaFilhoRepositoryCustom;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PessoaFilhoRepositoryCustomImpl implements PessoaFilhoRepositoryCustom {


    @PersistenceContext
    EntityManager entityManager;


    @Override
    @Transactional
    public void teste() {

//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        //CriteriaQuery<PessoaFilhoResponsePesquisaResultDTO> criteriaQuery = criteriaBuilder.createQuery(PessoaFilhoResponsePesquisaResultDTO.class);
//        CriteriaQuery<Tuple> criteriaQuery = criteriaBuilder.createTupleQuery();
//
//
//
//        Root<PessoaFilho> pessoaFilhoRoot = criteriaQuery.from(PessoaFilho.class);
//        final Join<PessoaFilho, Pessoa> joinPessoa = pessoaFilhoRoot.join("pessoa", JoinType.INNER);
//
//
//        criteriaQuery.multiselect(
//                joinPessoa.get("oidPessoa").alias("oidPessoa")
//                , joinPessoa.get("nomNome").alias("nomNomePessoa")
//                , pessoaFilhoRoot.get("oidPessoaFilho").alias("oidPessoaFilho")
//                , pessoaFilhoRoot.get("nomNome").alias("nomNomePessoaFilho")
//                , pessoaFilhoRoot.get("dtaNascimento").alias("dtaNascimento")
//
//        );
//
//        List<Tuple> tupleResult = entityManager.createQuery(criteriaQuery).getResultList();
//
//
//        List<PessoaFilhoResponseDTO> pessoaFilhoResponseDTOS = new ArrayList<>();
//
//
//        for (Tuple t:tupleResult) {
//
//            pessoaFilhoResponseDTOS.add(
//                    PessoaFilhoResponseDTO.builder()
//                            .oidPessoaFilho((Long) t.get("oidPessoaFilho"))
//                            .nomNome(t.get("nomNomePessoaFilho").toString())
//                            .dtaNascimento((Date) t.get("dtaNascimento"))
//                            .pessoaResponseDTO(
//                                    PessoaResponseDTO.builder()
//                                            .oidPessoa((Long) t.get("oidPessoa"))
//                                            .nomNome(t.get("nomNomePessoa").toString())
//                                            .build()
//                            )
//                            .build()
//            );
//        }

//        System.out.println(pessoaFilhoResponseDTOS);


//        TypedQuery<PessoaFilhoResponsePesquisaResultDTO> typedQuery = entityManager.createQuery(criteriaQuery);
//        System.out.println(typedQuery.getResultList());






    }
}
