package ru.khorunzhev.otus.homework3.model.mongo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Document(value = "author")
@ToString(exclude = "id")
public class Author {

    @Id
    private String id;
    private String fullName;

    public Author(String fullName) {
        this.fullName = fullName;
    }

}
