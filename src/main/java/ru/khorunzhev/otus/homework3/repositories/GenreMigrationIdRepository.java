package ru.khorunzhev.otus.homework3.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId;

import java.util.Optional;

public interface GenreMigrationIdRepository extends CrudRepository<GenreMigrationId, Long> {
    Optional<GenreMigrationId> findByNoSqlId(String id);
}
