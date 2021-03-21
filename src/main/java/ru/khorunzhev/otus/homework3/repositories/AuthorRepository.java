package ru.khorunzhev.otus.homework3.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.khorunzhev.otus.homework3.model.jpa.Author;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId;

import java.util.Optional;

public interface AuthorRepository extends CrudRepository<Author, Long> {
}
