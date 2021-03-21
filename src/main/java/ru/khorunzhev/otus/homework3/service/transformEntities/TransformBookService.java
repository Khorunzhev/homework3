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

    AuthorMigrationIdRepository authorMigrationIdRepository;
    GenreMigrationIdRepository genreMigrationIdRepository;
    AuthorRepository authorRepository;
    GenreRepository genreRepository;

    public Book transformBook(ru.khorunzhev.otus.homework3.model.mongo.Book mongoBook) {

        AuthorMigrationId authorMigrationId = authorMigrationIdRepository.findByNoSqlId(mongoBook.getAuthor().getId()).get();
        Author author = authorRepository.findById(authorMigrationId.getID()).get();

        GenreMigrationId genreMigrationId = genreMigrationIdRepository.findByNoSqlId(mongoBook.getGenre().getId()).get();
        Genre genre = genreRepository.findById(genreMigrationId.getID()).get();

        return Book.builder()
                .title(mongoBook.getTitle())
                .author(author)
                .genre(genre)
                .build();
    }
}
