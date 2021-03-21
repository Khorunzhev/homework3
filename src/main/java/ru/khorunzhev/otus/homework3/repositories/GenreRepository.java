package ru.khorunzhev.otus.homework3.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.khorunzhev.otus.homework3.model.jpa.Author;
import ru.khorunzhev.otus.homework3.model.jpa.Genre;

public interface GenreRepository extends CrudRepository<Genre, Long> {
}
