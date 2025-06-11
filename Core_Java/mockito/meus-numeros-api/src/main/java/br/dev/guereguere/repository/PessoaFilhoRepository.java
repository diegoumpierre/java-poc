package br.dev.guereguere.repository;


import br.dev.guereguere.entity.PessoaFilho;
import br.dev.guereguere.repository.custom.PessoaFilhoRepositoryCustom;
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
public interface PessoaFilhoRepository extends CrudRepository<PessoaFilho, Long>, PessoaFilhoRepositoryCustom {

    @Modifying
    @Transactional
    @Query("DELETE FROM PessoaFilho obj where obj.oidPessoaFilho = :oidPessoaFilho")
    void deleteByOid(@Param("oidPessoaFilho") Long oidPessoaFilho);

    @Modifying
    @Transactional
    @Query("DELETE FROM PessoaFilho obj WHERE obj.oidPessoaFilho in (:oidPessoaFilhoLst)")
    void deleteAllByOid(@Param("oidPessoaFilhoLst") Collection<Long> oidPessoaFilhoLst);


    @Transactional(readOnly=true)
    @Query(
            "SELECT DISTINCT obj FROM PessoaFilho obj WHERE 1=1 " 
		 +  " AND (:oidPessoaFilho IS NULL OR obj.oidPessoaFilho = :oidPessoaFilho) "
		 +  " AND (:oidPessoa IS NULL OR obj.pessoa.oidPessoa = :oidPessoa) "
		 +  " AND (:nomNomePessoa IS NULL OR obj.pessoa.nomNome='' OR obj.pessoa.nomNome like %:nomNomePessoa%) "
		 +  " AND (:nomNome IS NULL OR nomNome ='' OR obj.nomNome LIKE %:nomNome%)"
           )
    Page<PessoaFilho> search(
            Pageable pageRequest
			,@Param("oidPessoaFilho") Long oidPessoaFilho
			,@Param("oidPessoa") Long oidPessoa
			,@Param("nomNomePessoa") String nomNomePessoa
			,@Param("nomNome") String nomNome
    );

    @Transactional(readOnly = true)
	@Query("SELECT DISTINCT obj FROM PessoaFilho obj WHERE 1=1 "
		  +" AND (:oidPessoa IS NULL OR obj.pessoa.oidPessoa = :oidPessoa) ")
	List<PessoaFilho> findAllByOidPessoa(@Param("oidPessoa") Long oidPessoa);

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