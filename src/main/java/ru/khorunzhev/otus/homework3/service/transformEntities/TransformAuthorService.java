package ru.khorunzhev.otus.homework3.service.transformEntities;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.Author;
import ru.khorunzhev.otus.homework3.model.jpa.Book;
import ru.khorunzhev.otus.homework3.model.jpa.Genre;
import ru.khorunzhev.otus.homework3.repositories.AuthorMigrationIdRepository;
import ru.khorunzhev.otus.homework3.repositories.GenreMigrationIdRepository;

@Service
public class TransformAuthorService {

    public Author transformAuthor(ru.khorunzhev.otus.homework3.model.mongo.Author mongoAuthor) {
        return Author.builder()
                .fullName(mongoAuthor.getFullName())
                .build();
    }
}
