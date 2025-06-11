package br.dev.guereguere.repository;


import br.dev.guereguere.entity.SecurityUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface SecurityUserRepository extends JpaRepository<SecurityUser, Integer> {

    @Transactional(readOnly=true)
    SecurityUser findByEmail(String email);

}
