package ru.khorunzhev.otus.homework3.service;

import org.springframework.stereotype.Service;
import ru.khorunzhev.otus.homework3.model.jpa.*;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransformBookService {

    public Book transformBook(ru.khorunzhev.otus.homework3.model.mongo.Book mongoBook) {
        Author author = Author.builder().fullName(mongoBook.getAuthor().getFullName()).build();
        Genre genre = Genre.builder().name(mongoBook.getGenre().getName()).build();

        return Book.builder()
                .title(mongoBook.getTitle())
                .author(author)
                .genre(genre)
                .build();
    }
}
