package ru.khorunzhev.otus.homework3.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId;

import java.util.Optional;

public interface AuthorMigrationIdRepository extends CrudRepository<AuthorMigrationId, Long> {
    Optional<AuthorMigrationId> findByNoSqlId(String id);
}
