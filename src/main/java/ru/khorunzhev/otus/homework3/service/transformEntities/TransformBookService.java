package ru.khorunzhev.otus.homework3.service.transformEntities;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.*;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.AuthorMigrationId;
import ru.khorunzhev.otus.homework3.model.jpa.connectingEntities.GenreMigrationId;
import ru.khorunzhev.otus.homework3.repositories.AuthorMigrationIdRepository;
import ru.khorunzhev.otus.homework3.repositories.AuthorRepository;
import ru.khorunzhev.otus.homework3.repositories.GenreMigrationIdRepository;
import ru.khorunzhev.otus.homework3.repositories.GenreRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TransformBookService {

    private static final String MIGRATION_ERROR = "Сущность %s для установки связи с книгой не обнаружена";

    private final AuthorMigrationIdRepository authorMigrationIdRepository;
    private final GenreMigrationIdRepository genreMigrationIdRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public Book transformBook(ru.khorunzhev.otus.homework3.model.mongo.Book mongoBook) {

        AuthorMigrationId authorMigrationId = authorMigrationIdRepository.findByNoSqlId(mongoBook.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException(String.format(MIGRATION_ERROR, AuthorMigrationId.class.getSimpleName())));
        Author author = authorRepository.findById(authorMigrationId.getID())
                .orElseThrow(() -> new RuntimeException(String.format(MIGRATION_ERROR, Author.class.getSimpleName())));

        GenreMigrationId genreMigrationId = genreMigrationIdRepository.findByNoSqlId(mongoBook.getGenre().getId())
                .orElseThrow(() -> new RuntimeException(String.format(MIGRATION_ERROR, GenreMigrationId.class.getSimpleName())));
        Genre genre = genreRepository.findById(genreMigrationId.getID())
                .orElseThrow(() -> new RuntimeException(String.format(MIGRATION_ERROR, Genre.class.getSimpleName())));

        return Book.builder()
                .title(mongoBook.getTitle())
                .author(author)
                .genre(genre)
                .build();
    }
}
