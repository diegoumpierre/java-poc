package br.dev.guereguere.repository;


import br.dev.guereguere.entity.Pessoa;
import br.dev.guereguere.repository.custom.PessoaRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import java.util.Collection;
import java.util.List;


@Repository
public interface PessoaRepository extends CrudRepository<Pessoa, Long>, PessoaRepositoryCustom {

    @Modifying
    @Transactional
    @Query("DELETE FROM Pessoa obj where obj.oidPessoa = :oidPessoa")
    void deleteByOid(@Param("oidPessoa") Long oidPessoa);

    @Modifying
    @Transactional
    @Query("DELETE FROM Pessoa obj WHERE obj.oidPessoa in (:oidPessoaLst)")
    void deleteAllByOid(@Param("oidPessoaLst") Collection<Long> oidPessoaLst);


    @Transactional(readOnly=true)
    @Query(
            "SELECT DISTINCT obj FROM Pessoa obj WHERE 1=1 " 
		 +  " AND (:oidPessoa IS NULL OR obj.oidPessoa = :oidPessoa) "
		 +  " AND (:nomNome IS NULL OR nomNome ='' OR obj.nomNome LIKE %:nomNome%)"
           )
    Page<Pessoa> search(
            Pageable pageRequest
			,@Param("oidPessoa") Long oidPessoa
			,@Param("nomNome") String nomNome
    );

    
}
/*
		 +  " AND (:nomNomePessoa IS NULL OR obj.pessoa.nomNome='' OR obj.pessoa.nomNome like %:nomNomePessoa%) "
		 +  " AND (:nomNome IS NULL OR nomNome ='' OR obj.nomNome LIKE %:nomNome%)"
		 +  " AND (:dtaNascimento IS NULL OR (obj.dtaNascimento >= :dtaNascimento and obj.dtaNascimento <= :dtaNascimentoEnd))"
           )
    Page<PessoaFilho> search(
            Pageable pageRequest
@@ -46,8 +43,6 @@ public interface PessoaFilhoRepository extends CrudRepository<PessoaFilho, Long>
			,@Param("oidPessoa") Long oidPessoa
			,@Param("nomNomePessoa") String nomNomePessoa
			,@Param("nomNome") String nomNome
			,@Param("dtaNascimento") Date dtaNascimento
			,@Param("dtaNascimentoEnd") Date dtaNascimentoEnd
    );
    */