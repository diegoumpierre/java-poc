package com.poc.kanban.repository.jpa;

import com.poc.kanban.domain.BoardType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaRepositoryBoardType extends CrudRepository<BoardType, String> {

    Optional<BoardType> findByCode(String code);
}
